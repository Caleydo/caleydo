package org.caleydo.core.view.opengl.canvas.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import java.util.ArrayList;
import java.util.Set;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.HeatMapRenderStyle;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.parcoords.EStorageBasedVAType;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Rendering the HeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class HeatMap
	extends AStorageBasedView
{

	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;

	private boolean bRenderHorizontally = false;

	private Vec4f vecRotation = new Vec4f(-90, 0, 0, 1);

	private Vec3f vecTranslation;

	private float fAnimationDefaultTranslation = 0;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private SelectedElementRep elementRep;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public HeatMap(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum)
	{

		super(iGLCanvasID, sLabel, viewFrustum);

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.externalIDType(EIDType.DAVID).mappingType(
						EMappingType.EXPRESSION_INDEX_2_DAVID,
						EMappingType.DAVID_2_EXPRESSION_STORAGE_ID).build();
		storageSelectionManager = new GenericSelectionManager.Builder(
				EIDType.EXPRESSION_EXPERIMENT).build();

		colorMapper = new ColorMapping(0, 1);
	}


	@Override
	public void init(GL gl)
	{

		bRenderStorageHorizontally = true;
		initData();
		initLists();

		renderStyle = new HeatMapRenderStyle(viewFrustum, contentSelectionManager, set,
				iContentVAID, set.getVA(iStorageVAID).size(), true);

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public void initLocal(GL gl)
	{
		renderOnlyContext(false);
		eWhichContentSelection = EStorageBasedVAType.COMPLETE_SELECTION;
		bRenderHorizontally = true;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		eWhichContentSelection = EStorageBasedVAType.EXTERNAL_SELECTION;
		bRenderHorizontally = true;

		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager, iUniqueID,
				iRemoteViewID, new Vec3f(0, 0, 0), layer, true, renderStyle);

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void displayLocal(GL gl)
	{

		if (bIsTranslationAnimationActive)
		{
			doTranslation();
		}

		pickingManager.handlePicking(iUniqueID, gl, true);

		if (bIsDisplayListDirtyLocal)
		{
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void displayRemote(GL gl)
	{

		if (bIsTranslationAnimationActive)
		{
			bIsDisplayListDirtyRemote = true;
			doTranslation();
		}

		if (bIsDisplayListDirtyRemote)
		{
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
		// pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void display(GL gl)
	{

		// gl.glCallList(iGLDisplayListToCall);
		buildDisplayList(gl, iGLDisplayListIndexRemote);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex)
	{

		// gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);
		gl.glClearStencil(0); // Clear The Stencil Buffer To 0
		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		gl.glDisable(GL.GL_DEPTH_TEST);

		// Clip region that renders in stencil buffer (in this case the frustum)
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		bRenderHorizontally = false;
		if (!bRenderHorizontally)
		{
			gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
			gl.glRotatef(vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation.w());
		}

		gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);

		renderHeatMap(gl);
		renderSelection(gl, ESelectionType.MOUSE_OVER);
		renderSelection(gl, ESelectionType.SELECTION);

		gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

		if (!bRenderHorizontally)
		{
			gl.glRotatef(-vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation.w());
			gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());
		}

		gl.glDisable(GL.GL_STENCIL_TEST);

		// gl.glEndList();
	}

	public void renderHorizontally(boolean bRenderHorizontally)
	{

		this.bRenderHorizontally = bRenderHorizontally;
	}

	protected void initLists()
	{

		Set<Integer> setMouseOver = storageSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);

		iContentVAID = mapSelections.get(eWhichContentSelection);
		iStorageVAID = mapSelections.get(eWhichStorageSelection);

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(set.getVA(iContentVAID));

		if (renderStyle != null)
		{
			renderStyle.setContentSelection(iContentVAID);
		}

		int iNumberOfRowsToRender = set.getVA(iStorageVAID).size();
		int iNumberOfColumns = set.getVA(iContentVAID).size();

		for (int iRowCount = 0; iRowCount < iNumberOfRowsToRender; iRowCount++)
		{
			contentSelectionManager.initialAdd(set.getVA(iStorageVAID).get(iRowCount));
		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++)
		{
			storageSelectionManager.initialAdd(set.getVA(iContentVAID).get(iColumnCount));
			if (setMouseOver.contains(set.getVA(iContentVAID).get(iColumnCount)))
			{
				storageSelectionManager.addToType(ESelectionType.MOUSE_OVER, set.getVA(
						iContentVAID).get(iColumnCount));
			}
		}
	}

	@Override
	public ArrayList<String> getInfo()
	{

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Heat Map");
		alInfo.add(set.getVA(iContentVAID).size() + " gene expression values");
		return alInfo;
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

		if (remoteRenderingGLCanvas != null)
		{
			// Check if selection occurs in the pool or memo layer of the remote
			// rendered view (i.e. bucket, jukebox)
			if (remoteRenderingGLCanvas.getHierarchyLayerByGLCanvasListenerId(iUniqueID)
					.getCapacity() > 5)
			{
				return;
			}
		}

		switch (pickingType)
		{
			case HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						connectedElementRepresentationManager.clear();
						// iAlOldSelection =
						// prepareSelection(storageSelectionManager,
						// EViewInternalSelectionType.SELECTION);

						storageSelectionManager.clearSelection(ESelectionType.SELECTION);
						storageSelectionManager.addToType(ESelectionType.SELECTION,
								iExternalID);

						if (eFieldDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(storageSelectionManager.getDelta());
							// propagateGeneSelection(iExternalID, 2,
							// iAlOldSelection);

						}

						break;

					case MOUSE_OVER:
						connectedElementRepresentationManager.clear();
						// iAlOldSelection =
						// prepareSelection(storageSelectionManager,
						// EViewInternalSelectionType.SELECTION);

						storageSelectionManager.clearSelection(ESelectionType.MOUSE_OVER);
						storageSelectionManager.addToType(ESelectionType.MOUSE_OVER,
								iExternalID);

						if (eFieldDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(storageSelectionManager.getDelta());
							// propagateGeneSelection(iExternalID, 1,
							// iAlOldSelection);
							// generalManager.getSingelton().
							// getViewGLCanvasManager().
							// getInfoAreaManager()
							// .setData(iUniqueID,
							// getAccesionIDFromStorageIndex(iExternalID),
							// EInputDataType.GENE, getInfo());
						}
						break;
				}

				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;

				pickingManager.flushHits(iUniqueID, pickingType);
				break;
		}
	}

	private void renderHeatMap(final GL gl)
	{

		float fXPosition = 0;
		float fYPosition = 0;
		// renderStyle.clearFieldWidths();

		// TODO: NullPointer if storage is empty
		Vec2f vecFieldWidthAndHeight = null;

		String sContent = "";
		// for(Integer iStorageIndex : alStorageSelection)
		// {
		// sContent = "Experiment " +iStorageIndex; // FIXME: from where should
		// we get a proper name?
		//			
		// // Render heat map experiment name
		// gl.glRotatef(45, 0, 0, 1);
		// textRenderer.setColor(0, 0, 0, 1);
		// textRenderer.begin3DRendering();
		// textRenderer.draw3D(sContent,
		// fYPosition,
		// -fXPosition,
		// 0.01f,
		// renderStyle.getHeadingFontScalingFactor());
		// textRenderer.end3DRendering();
		// gl.glRotatef(-45, 0, 0, 1);
		// }

		int iCount = 0;
		for (Integer iContentIndex : set.getVA(iContentVAID))
		{
			vecFieldWidthAndHeight = renderStyle.getAndInitFieldWidthAndHeight(iCount);
			fYPosition = renderStyle.getYCenter() - vecFieldWidthAndHeight.y()
					* set.getVA(iStorageVAID).size() / 2;

			for (Integer iStorageIndex : set.getVA(iStorageVAID))
			{
				renderElement(gl, iStorageIndex, iContentIndex, fXPosition, fYPosition,
						vecFieldWidthAndHeight);

				fYPosition += vecFieldWidthAndHeight.y();
			}

			float fFontScaling = 0;
			if (vecFieldWidthAndHeight.x() > 0.1f)
			{
				if (vecFieldWidthAndHeight.x() < 0.2f)
				{
					fFontScaling = renderStyle.getSmallFontScalingFactor();
				}
				else
				{
					fFontScaling = renderStyle.getHeadingFontScalingFactor();
				}

				// Render heat map element name
				gl.glRotatef(90, 0, 0, 1);
				sContent = getRefSeqFromStorageIndex(iContentIndex);
				textRenderer.setColor(0, 0, 0, 1);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(sContent, (fYPosition + vecFieldWidthAndHeight.y() / 3),
						-(fXPosition + vecFieldWidthAndHeight.x() / 2),// -
						// renderStyle
						// .
						// getXCenter
						// (),
						0.01f, fFontScaling);
				textRenderer.end3DRendering();
				gl.glRotatef(-90, 0, 0, 1);
			}
			iCount++;
			fXPosition += vecFieldWidthAndHeight.x();
		}
	}

	private void renderElement(final GL gl, final int iStorageIndex, final int iContentIndex,
			final float fXPosition, final float fYPosition, final Vec2f vecFieldWidthAndHeight)
	{

		float fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
				iContentIndex);
		Vec3f vecMappingColor = colorMapper.colorMappingLookup(fLookupValue);
		gl.glColor3f(vecMappingColor.x(), vecMappingColor.y(), vecMappingColor.z());

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HEAT_MAP_FIELD_SELECTION, iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition,
				HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition
				+ vecFieldWidthAndHeight.y(), HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + vecFieldWidthAndHeight.y(),
				HeatMapRenderStyle.FIELD_Z);
		gl.glEnd();

		gl.glPopName();
	}

	private void renderSelection(final GL gl, ESelectionType eSelectionType)
	{

		Set<Integer> selectedSet = storageSelectionManager.getElements(eSelectionType);
		float fHeight = 0;
		float fXPosition = 0;
		float fYPosition = 0;

		switch (eSelectionType)
		{
			case SELECTION:
				gl.glColor4fv(HeatMapRenderStyle.SELECTED_COLOR, 0);
				gl.glLineWidth(HeatMapRenderStyle.SELECTED_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				gl.glColor4fv(HeatMapRenderStyle.MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(HeatMapRenderStyle.MOUSE_OVER_LINE_WIDTH);
				break;
		}

		for (Integer iCurrentColumn : selectedSet)
		{
			int iColumnIndex = set.getVA(iContentVAID).indexOf(iCurrentColumn);
			if (iColumnIndex == -1)
				continue;
			Vec2f vecFieldWidthAndHeight = renderStyle.getFieldWidthAndHeight(iColumnIndex);

			fHeight = set.getVA(iStorageVAID).size() * vecFieldWidthAndHeight.y();
			fXPosition = renderStyle.getXDistanceAt(iColumnIndex);
			fYPosition = renderStyle.getYCenter() - vecFieldWidthAndHeight.y()
					* set.getVA(iStorageVAID).size() / 2;

			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(fXPosition, fYPosition, HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition,
					HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition + fHeight,
					HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition, fYPosition + fHeight, HeatMapRenderStyle.SELECTION_Z);
			gl.glEnd();

			fHeight = 0;
			fXPosition = 0;
		}
	}

	@Override
	protected SelectedElementRep createElementRep(int iStorageIndex)
	throws InvalidAttributeValueException
	{
				
		//SelectedElementRep elementRep = new SelectedElementRep(iUniqueID, 0.0f, 0.0f, 0.0f);
		
		int iContentIndex = set.getVA(iContentVAID).indexOf(iStorageIndex);
		renderStyle.clearFieldWidths();
		Vec2f vecFieldWithAndHeight = null;

		for (int iCount = 0; iCount <= iContentIndex; iCount++)
		{
			vecFieldWithAndHeight = renderStyle.getAndInitFieldWidthAndHeight(iCount);
		}

		float fXValue = renderStyle.getXDistanceAt(iContentIndex) + vecFieldWithAndHeight.x()
				/ 2;// + renderStyle.getXSpacing();

		float fYValue = renderStyle.getYCenter() + vecFieldWithAndHeight.y()
				* set.getVA(iContentVAID).size() / 2;

		if (bRenderHorizontally)
		{
			elementRep = new SelectedElementRep(iUniqueID, fXValue + fAnimationTranslation,
					fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(iUniqueID, vecPoint.x(), vecPoint.y()
					- fAnimationTranslation, 0);

		}
		return elementRep;
	}

	@Override
	protected void rePosition(int iElementID)
	{

		int iSelection;
		if (bRenderStorageHorizontally)
			iSelection = iContentVAID;
		else
			iSelection = iStorageVAID;
		// TODO test this

		float fCurrentPosition = set.getVA(iSelection).indexOf(iElementID)
				* renderStyle.getNormalFieldWidth();// +
		// renderStyle.getXSpacing(
		// );

		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength = (set.getVA(iSelection).size() - 1) * renderStyle.getNormalFieldWidth()
				+ 1.5f; // MARC
		// :
		// 1.5
		// =
		// correction of
		// lens effect in
		// heatmap

		fAnimationTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);

		if (-fAnimationTargetTranslation > fLength - fFrustumLength)
			fAnimationTargetTranslation = -(fLength - fFrustumLength + 2 * 0.00f);
		else if (fAnimationTargetTranslation > 0)
			fAnimationTargetTranslation = 0;
		else if (-fAnimationTargetTranslation < -fAnimationTranslation + fFrustumLength / 2
				- 0.00f
				&& -fAnimationTargetTranslation > -fAnimationTranslation - fFrustumLength / 2
						+ 0.00f)
		{
			fAnimationTargetTranslation = fAnimationTranslation;
			return;
		}

		bIsTranslationAnimationActive = true;
	}

	private void doTranslation()
	{

		float fDelta = 0;
		if (fAnimationTargetTranslation < fAnimationTranslation - 0.5f)
		{

			fDelta = -0.5f;

		}
		else if (fAnimationTargetTranslation > fAnimationTranslation + 0.5f)
		{
			fDelta = 0.5f;
		}
		else
		{
			fDelta = fAnimationTargetTranslation - fAnimationTranslation;
			bIsTranslationAnimationActive = false;
		}

		if (elementRep != null)
		{
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for (Vec3f currentPoint : alPoints)
			{
				currentPoint.setY(currentPoint.y() - fDelta);
			}
		}

		fAnimationTranslation += fDelta;
	}
}
