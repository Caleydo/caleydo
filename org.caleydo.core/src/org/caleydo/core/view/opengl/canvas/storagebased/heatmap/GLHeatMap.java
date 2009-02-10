package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;
import org.caleydo.core.view.opengl.miniview.GLColorMappingBarMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap
	extends AStorageBasedView
{

	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private GLColorMappingBarMiniView colorMappingBar;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	// private boolean bRenderHorizontally = false;

	private Vec4f vecRotation = new Vec4f(-90, 0, 0, 1);

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private SelectedElementRep elementRep;

	private ArrayList<Float> fAlXDistances;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHeatMap(ESetType setType, final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(setType, iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.externalIDType(EIDType.REFSEQ_MRNA_INT).mappingType(
						EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT,
						EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX)
				.build();

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		colorMappingBar = new GLColorMappingBarMiniView(viewFrustum);
		// TODO use constant instead
		iNumberOfRandomElements = generalManager.getPreferenceStore().getInt(
				"hmNumRandomSamplinPoints");
		fAlXDistances = new ArrayList<Float>();
	}

	@Override
	public void init(GL gl)
	{
		// iconTextureManager = new GLIconTextureManager(gl);
		initData();

		colorMappingBar.setHeight(renderStyle.getColorMappingBarHeight());
		colorMappingBar.setWidth(renderStyle.getColorMappingBarWidth());
		if (set == null)
			return;
	}

	@Override
	public void initLocal(GL gl)
	{
		dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		bRenderOnlyContext = false;

		generalManager.getEventPublisher().addSender(EMediatorType.PROPAGATION_MEDIATOR, this);

		bRenderStorageHorizontally = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas)
	{
		dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
		bRenderOnlyContext = true;

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		bRenderStorageHorizontally = false;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel)
	{
		super.setDetailLevel(detailLevel);
		renderStyle.setDetailLevel(detailLevel);
		renderStyle.updateFieldSizes();
	}

	@Override
	public synchronized void displayLocal(GL gl)
	{
		if (set == null)
			return;

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

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);
	}

	@Override
	public synchronized void displayRemote(GL gl)
	{
		if (set == null)
			return;

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
	public synchronized void display(GL gl)
	{
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// GLHelperFunctions.drawAxis(gl);
		gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex)
	{

		if (bHasFrustumChanged)
		{
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentSelectionManager.getNumberOfElements() == 0)
		{
			renderSymbol(gl);
		}
		else
		{

			// FIXME: bad hack, normalize frustum to 0:1 to avoid that
			// clipToFrustum(gl);
			float fLeftOffset = 0;
			if (remoteRenderingGLCanvas == null)
				fLeftOffset = 0.05f;
			else
				fLeftOffset = 0.15f;
			// GLHelperFunctions.drawAxis(gl);
			// if (detailLevel == EDetailLevel.HIGH)
			// {
			// colorMappingBar.render(gl, fLeftOffset,
			// (viewFrustum.getHeight() - colorMappingBar.getHeight()) / 2,
			// 0.2f);
			// gl.glTranslatef(fLeftOffset + colorMappingBar.getWidth(), 0, 0);
			// }

			if (!bRenderStorageHorizontally)
			{
				gl.glTranslatef(vecTranslation.x(), viewFrustum.getHeight(), vecTranslation
						.z());
				gl.glRotatef(vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation
						.w());
			}

			gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);

			renderHeatMap(gl);

			renderSelection(gl, ESelectionType.MOUSE_OVER);
			renderSelection(gl, ESelectionType.SELECTION);

			gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

			if (!bRenderStorageHorizontally)
			{
				gl.glRotatef(-vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation
						.w());
				gl.glTranslatef(-vecTranslation.x(), -viewFrustum.getHeight(), -vecTranslation
						.z());
			}
			if (detailLevel == EDetailLevel.HIGH)
			{
				gl.glTranslatef(-fLeftOffset - colorMappingBar.getWidth(), 0, 0);
			}

			gl.glDisable(GL.GL_STENCIL_TEST);
		}
		gl.glEndList();
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl)
	{
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	public synchronized void renderHorizontally(boolean bRenderStorageHorizontally)
	{

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	@Override
	protected void initLists()
	{

		// Set<Integer> setMouseOver = storageSelectionManager
		// .getElements(ESelectionType.MOUSE_OVER);

		if (bRenderOnlyContext)
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		storageSelectionManager.setVA(set.getVA(iStorageVAID));

		if (renderStyle != null)
		{
			renderStyle.setActiveVirtualArray(iContentVAID);
		}

		int iNumberOfColumns = set.getVA(iContentVAID).size();
		int iNumberOfRows = set.getVA(iStorageVAID).size();

		for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++)
		{
			storageSelectionManager.initialAdd(set.getVA(iStorageVAID).get(iRowCount));

		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++)
		{
			contentSelectionManager.initialAdd(set.getVA(iContentVAID).get(iColumnCount));

			// if
			// (setMouseOver.contains(set.getVA(iContentVAID).get(iColumnCount
			// )))
			// {
			// storageSelectionManager.addToType(ESelectionType.MOUSE_OVER,
			// set.getVA(
			// iContentVAID).get(iColumnCount));
			// }
		}

		renderStyle = new HeatMapRenderStyle(viewFrustum, contentSelectionManager, set,
				iContentVAID, iStorageVAID, set.getVA(iStorageVAID).size(),
				bRenderStorageHorizontally);
		renderStyle.setDetailLevel(detailLevel);
		super.renderStyle = renderStyle;

		// TODO probably remove this here
		// renderStyle.initFieldSizes();

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public String getShortInfo()
	{
		return "Heat Map (" + set.getVA(iContentVAID).size() + " genes / "
				+ set.getVA(iStorageVAID).size() + " experiments)";
	}

	@Override
	public String getDetailedInfo()
	{
		// StringBuffer sInfoText = new StringBuffer();
		// sInfoText.append("Heat Map");
		// sInfoText.append(set.getVA(iContentVAID).size() +
		// " gene expression values");
		// return sInfoText.toString();

		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		if (bRenderStorageHorizontally)
			sInfoText.append(set.getVA(iContentVAID).size() + "Genes in columns and "
					+ set.getVA(iStorageVAID).size() + " experiments in rows.\n");
		else
			sInfoText.append(set.getVA(iContentVAID).size() + " Genes in rows and "
					+ set.getVA(iStorageVAID).size() + " experiments in columns.\n");

		if (bRenderOnlyContext)
		{
			sInfoText
					.append("Showing only genes which occur in one of the other views in focus\n");
		}
		else
		{
			if (bUseRandomSampling)
			{
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			}
			else
			{
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE)
				sInfoText.append("Showing all genes in the dataset\n");
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING)
				sInfoText.append("Showing all genes that have a known DAVID ID mapping\n");
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT)
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
		}

		return sInfoText.toString();
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		if (detailLevel == EDetailLevel.VERY_LOW)
		{
			pickingManager.flushHits(iUniqueID, ePickingType);
			return;
		}
		ESelectionType eSelectionType;
		switch (ePickingType)
		{
			case HEAT_MAP_LINE_SELECTION:

				switch (pickingMode)
				{
					case DOUBLE_CLICKED:
						IDListEventContainer<Integer> idListEventContainer = new IDListEventContainer<Integer>(
								EEventType.LOAD_PATHWAY_BY_GENE, EIDType.REFSEQ_MRNA_INT);
						idListEventContainer.addID(getRefSeqFromStorageIndex(iExternalID));
						triggerEvent(EMediatorType.SELECTION_MEDIATOR, idListEventContainer);
						// intentionally no break

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
						return;

				}

				if (contentSelectionManager.checkStatus(eSelectionType, iExternalID))
					break;

				connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);

				contentSelectionManager.clearSelection(eSelectionType);

				// Resolve multiple spotting on chip and add all to the
				// selection manager.
				Integer iRefSeqID = idMappingManager.getID(
						EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iExternalID);
				for (Object iExpressionIndex : idMappingManager.getMultiID(
						EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID))
				{
					contentSelectionManager.addToType(eSelectionType,
							(Integer) iExpressionIndex);
				}

				contentSelectionManager.addConnectionID(generalManager.getIDManager()
						.createID(EManagedObjectType.CONNECTION), iExternalID);

				if (eFieldDataType == EIDType.EXPRESSION_INDEX)
				{
					ISelectionDelta selectionDelta = contentSelectionManager.getDelta();

					triggerEvent(EMediatorType.SELECTION_MEDIATOR,
							new SelectionCommandEventContainer(EIDType.REFSEQ_MRNA_INT,
									new SelectionCommand(ESelectionCommandType.CLEAR,
											eSelectionType)));

					handleConnectedElementRep(selectionDelta);
					triggerEvent(EMediatorType.SELECTION_MEDIATOR,
							new DeltaEventContainer<ISelectionDelta>(selectionDelta));
				}

				setDisplayListDirty();
				break;

			case HEAT_MAP_STORAGE_SELECTION:

				switch (pickingMode)
				{
					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
						return;
				}

				storageSelectionManager.clearSelection(eSelectionType);
				storageSelectionManager.addToType(eSelectionType, iExternalID);

				if (eStorageDataType == EIDType.EXPERIMENT_INDEX)
				{

					triggerEvent(EMediatorType.SELECTION_MEDIATOR,
							new SelectionCommandEventContainer(EIDType.EXPERIMENT_INDEX,
									new SelectionCommand(ESelectionCommandType.CLEAR,
											eSelectionType)));
					ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
					triggerEvent(EMediatorType.SELECTION_MEDIATOR,
							new DeltaEventContainer<ISelectionDelta>(selectionDelta));
				}
				setDisplayListDirty();
				break;

		}

		pickingManager.flushHits(iUniqueID, ePickingType);

	}

	private void renderHeatMap(final GL gl)
	{
		fAlXDistances.clear();
		renderStyle.updateFieldSizes();
		float fXPosition = 0;
		float fYPosition = 0;
		float fFieldWith = 0;
		float fFieldHeight = 0;
		// renderStyle.clearFieldWidths();

		int iCount = 0;
		for (Integer iContentIndex : set.getVA(iContentVAID))
		{
			iCount++;
			if (contentSelectionManager.checkStatus(ESelectionType.NORMAL, iContentIndex))
			{
				fFieldWith = renderStyle.getNormalFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
			}
			else if (contentSelectionManager.checkStatus(ESelectionType.SELECTION,
					iContentIndex)
					|| contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER,
							iContentIndex))
			{
				fFieldWith = renderStyle.getSelectedFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
			}

			fYPosition = 0;

			for (Integer iStorageIndex : set.getVA(iStorageVAID))
			{
				renderElement(gl, iStorageIndex, iContentIndex, fXPosition, fYPosition,
						fFieldWith, fFieldHeight);
				fYPosition += fFieldHeight;

			}

			float fFontScaling = 0;

			float fColumnDegrees = 0;
			float fLineDegrees = 0;
			if (bRenderStorageHorizontally)
			{
				fColumnDegrees = 0;
				fLineDegrees = 25;
			}
			else
			{
				fColumnDegrees = 60;
				fLineDegrees = 90;
			}

			// render line captions
			if (fFieldWith > 0.1f)
			{
				boolean bRenderRefSeq = false;
				if (fFieldWith < 0.2f)
				{
					fFontScaling = renderStyle.getSmallFontScalingFactor();
				}
				else
				{
					bRenderRefSeq = true;
					fFontScaling = renderStyle.getHeadingFontScalingFactor();
				}

				if (detailLevel == EDetailLevel.HIGH)
				{
					String sContent;

					sContent = getShortNameFromDavid(iContentIndex);
					if (sContent == null)
						sContent = "Unknown";
					

					if (bRenderRefSeq)
					{
						sContent += " | ";
						// Render heat map element name
						sContent += getRefSeqStringFromStorageIndex(iContentIndex);
					}
					// if (sContent == null)
					// sContent = "Unknown";
					// renderCaption(gl, sContent, fXPosition + fFieldWith / 6 *
					// 2.5f,
					// fYPosition + 0.1f, fLineDegrees, fFontScaling);

					renderCaption(gl, sContent, fXPosition + fFieldWith / 6 * 4.5f,
							fYPosition + 0.1f, fLineDegrees, fFontScaling);
				}

			}
			// renderStyle.setXDistanceAt(set.getVA(iContentVAID).indexOf(iContentIndex),
			// fXPosition);
			fAlXDistances.add(fXPosition);
			fXPosition += fFieldWith;

			// render column captions
			if (detailLevel == EDetailLevel.HIGH)
			{
				if (iCount == set.getVA(iContentVAID).size())
				{
					fYPosition = 0;
					for (Integer iStorageIndex : set.getVA(iStorageVAID))
					{
						renderCaption(gl, set.get(iStorageIndex).getLabel(),
								fXPosition + 0.1f, fYPosition + fFieldHeight / 2,
								fColumnDegrees, renderStyle.getSmallFontScalingFactor());
						fYPosition += fFieldHeight;
					}
				}
			}
		}
	}

	private void renderElement(final GL gl, final int iStorageIndex, final int iContentIndex,
			final float fXPosition, final float fYPosition, final float fFieldWidth,
			final float fFieldHeight)
	{

		float fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
				iContentIndex);

		float fOpacity = 0;
		if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)
				|| contentSelectionManager
						.checkStatus(ESelectionType.SELECTION, iContentIndex)
				|| detailLevel.compareTo(EDetailLevel.LOW) > 0)
			fOpacity = 1f;
		else
			fOpacity = 0.3f;

		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HEAT_MAP_LINE_SELECTION, iContentIndex));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HEAT_MAP_STORAGE_SELECTION, iStorageIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderSelection(final GL gl, ESelectionType eSelectionType)
	{

		// content selection

		Set<Integer> selectedSet = contentSelectionManager.getElements(eSelectionType);
		float fHeight = 0;
		float fXPosition = 0;
		float fYPosition = 0;

		switch (eSelectionType)
		{
			case SELECTION:
				gl.glColor4fv(SELECTED_COLOR, 0);
				gl.glLineWidth(SELECTED_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
				break;
		}

		int iColumnIndex = 0;
		for (int iTempColumn : set.getVA(iContentVAID))
		{
			for (Integer iCurrentColumn : selectedSet)
			{

				if (iCurrentColumn == iTempColumn)
				{
					fHeight = set.getVA(iStorageVAID).size() * renderStyle.getFieldHeight();
					fXPosition = fAlXDistances.get(iColumnIndex);

					fYPosition = 0;

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(fXPosition, fYPosition, SELECTION_Z);
					gl.glVertex3f(fXPosition + renderStyle.getSelectedFieldWidth(),
							fYPosition, SELECTION_Z);
					gl.glVertex3f(fXPosition + renderStyle.getSelectedFieldWidth(), fYPosition
							+ fHeight, SELECTION_Z);
					gl.glVertex3f(fXPosition, fYPosition + fHeight, SELECTION_Z);
					gl.glEnd();

					fHeight = 0;
					fXPosition = 0;
				}
			}
			iColumnIndex++;
		}

		// storage selection

		selectedSet = storageSelectionManager.getElements(eSelectionType);
		int iLineIndex = 0;
		for (int iTempLine : set.getVA(iStorageVAID))
		{
			for (Integer iCurrentLine : selectedSet)
			{
				if (iTempLine == iCurrentLine)
				{
					// TODO we need indices of all elements

					fYPosition = iLineIndex * renderStyle.getFieldHeight();
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(0, fYPosition, SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition, SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition
							+ renderStyle.getFieldHeight(), SELECTION_Z);
					gl.glVertex3f(0, fYPosition + renderStyle.getFieldHeight(), SELECTION_Z);
					gl.glEnd();
				}
			}
			iLineIndex++;
		}
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta)
	{
		renderStyle.updateFieldSizes();
		fAlXDistances.clear();
		float fDistance = 0;

		for (Integer iStorageIndex : set.getVA(iContentVAID))
		{
			fAlXDistances.add(fDistance);
			if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iStorageIndex)
					|| contentSelectionManager.checkStatus(ESelectionType.SELECTION,
							iStorageIndex))
			{
				fDistance += renderStyle.getSelectedFieldWidth();
			}
			else
			{
				fDistance += renderStyle.getNormalFieldWidth();
			}

		}
		super.handleConnectedElementRep(selectionDelta);
	}

	@Override
	protected SelectedElementRep createElementRep(EIDType idType, int iStorageIndex)
			throws InvalidAttributeValueException
	{

		SelectedElementRep elementRep;

		int iContentIndex = set.getVA(iContentVAID).indexOf(iStorageIndex);
		if (iContentIndex == -1)
		{
			throw new IllegalStateException("No element in virtual array for storage index");
			// TODO this shouldn't happen
			// generalManager.getLogger().log(Level.SEVERE,
			// "No element in virtual array for storage index");
			//
			// return null;
		}

		float fXValue = fAlXDistances.get(iContentIndex) + renderStyle.getSelectedFieldWidth()
				/ 2;

		float fYValue = renderStyle.getYCenter();

		if (bRenderStorageHorizontally)
		{
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, fXValue
					+ fAnimationTranslation, fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, vecPoint
					.x(), vecPoint.y() - fAnimationTranslation, 0);

		}
		return elementRep;
	}

	/**
	 * Re-position a view centered on a element, specified by the element ID
	 * 
	 * @param iElementID the ID of the element that should be in the center
	 */
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

	@Override
	public void renderContext(boolean bRenderOnlyContext)
	{

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext)
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();

			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin,
			float fRotation, float fFontScaling)
	{

		if (sLabel.length() > HeatMapRenderStyle.NUM_CHAR_LIMIT + 1)
		{
			sLabel = sLabel.substring(0, HeatMapRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, 0);
		gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, 0, 0, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glRotatef(-fRotation, 0, 0, 1);
		gl.glTranslatef(-fXOrigin, -fYOrigin, 0);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
	}

	@Override
	public synchronized void broadcastElements()
	{
		ISelectionDelta delta = contentSelectionManager.getCompleteDelta();
		triggerEvent(EMediatorType.SELECTION_MEDIATOR,
				new DeltaEventContainer<ISelectionDelta>(delta));
		setDisplayListDirty();
	}

	@Override
	public synchronized void resetSelections()
	{
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();
		setDisplayListDirty();
	}

	@Override
	public void changeOrientation(boolean defaultOrientation)
	{
		renderHorizontally(defaultOrientation);
	}

	@Override
	public boolean isInDefaultOrientation()
	{
		return bRenderStorageHorizontally;
	}

}
