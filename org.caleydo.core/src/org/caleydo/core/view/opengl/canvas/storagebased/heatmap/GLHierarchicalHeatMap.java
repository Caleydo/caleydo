package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.VADeltaItem;
import org.caleydo.core.data.selection.VirtualArrayDelta;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.spline.Spline3D;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLIconTextureManager;
import org.eclipse.core.runtime.preferences.PreferenceModifyListener;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Rendering the GLHierarchicalHeatMap with remote rendering support.
 * 
 * @author Bernhard Schlegl
 * @author Marc Streit
 */
public class GLHierarchicalHeatMap
	extends AStorageBasedView
{
	private final static float GAP_LEVEL1_2 = 0.6f;
	private final static float GAP_LEVEL2_3 = 0.4f;

	private final static float MAX_NUM_SAMPLES = 8f;

	private final static int MIN_SAMPLES_PER_HEATMAP = 10;
	private final static int MAX_SAMPLES_PER_HEATMAP = 50;

	private int iSamplesPerTexture = 0;

	private int iSamplesPerHeatmap = 0;

	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;

	// private boolean bRenderHorizontally = false;

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private GLIconTextureManager iconTextureManager;

	private ArrayList<Float> fAlXDistances;

	// selector for texture in overviewBar
	private int iSelectorBar = 1;

	// number of partitions for selection in overViewBar
	private int iNrSelBar = 0;

	// array of textures for holding the data samples
	private Texture[] THeatMap;
	private ArrayList<Integer> iAlNumberSamples = new ArrayList<Integer>();


	private Point PickingPoint = null;
	private int iPickedSample = 0;
	private int iFirstSample = 0;
	private int iLastSample = 0;

	private ArrayList<HeatMapSelection> AlSelection = new ArrayList<HeatMapSelection>();

	private boolean bRenderCaption;

	private float fAnimationScale = 1.0f;

	// embedded heat map
	private GLHeatMap glHeatMapView;

	private boolean bIsHeatmapInFocus = false;

	// dragging stuff
	private boolean bIsDraggingActive = false;
	private int iDraggedCursor = 0;
	private float fPosCursorFirstElement = 0;
	private float fPosCursorLastElement = 0;

	/**
	 * Constructor.
	 * 
	 * @param setType
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHierarchicalHeatMap(ESetType setType, final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(setType, iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HIER_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.externalIDType(EIDType.DAVID).mappingType(
						EMappingType.EXPRESSION_INDEX_2_DAVID,
						EMappingType.DAVID_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX)
				.build();

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		// TODO use constant instead
		iNumberOfRandomElements = generalManager.getPreferenceStore().getInt(
				"hmNumRandomSamplinPoints");

		// default: 500 (PreferenceInitializer)
		iSamplesPerTexture = generalManager.getPreferenceStore().getInt(
				"hmNumSamplesPerTexture");

		// default: 30 (PreferenceInitializer)
		iSamplesPerHeatmap = generalManager.getPreferenceStore().getInt(
				"hmNumSamplesPerHeatmap");

		fAlXDistances = new ArrayList<Float>();

		generalManager.getEventPublisher().addSender(EMediatorType.HIERACHICAL_HEAT_MAP, this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.HIERACHICAL_HEAT_MAP,
				this);
	}

	@Override
	public void init(GL gl)
	{
		bRenderOnlyContext = false;
		createHeatMap();

		// FIXME: the two nulls here break the interface, it is not possible to
		// determine whether a view is rendered remote with this initialization
		glHeatMapView.initRemote(gl, getID(), pickingTriggerMouseAdapter, null);

		iconTextureManager = new GLIconTextureManager();
		initData();

		if (set == null)
			return;

		initTextures(gl);
		initPosCursor();
	}

	@Override
	public void initLocal(GL gl)
	{

		bRenderStorageHorizontally = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(GL gl, int remoteViewID,
			PickingJoglMouseListener pickingTriggerMouseAdapter,
			IGLCanvasRemoteRendering remoteRenderingGLCanvas)
	{

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		bRenderStorageHorizontally = false;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	/**
	 * Init (reset) the positions of cursors used for highlighting selected
	 * elements in stage 2 (texture)
	 * 
	 * @param
	 */
	private void initPosCursor()
	{
		iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
		iFirstSample = 0;
		iLastSample = iSamplesPerHeatmap - 1;
	}

	/**
	 * Init textures, build array of textures used for holding the whole
	 * examples from contentSelectionManager
	 * 
	 * @param gl
	 */
	private void initTextures(GL gl)
	{
		fAlXDistances.clear();
		renderStyle.updateFieldSizes();

		iNrSelBar = (int) Math.ceil(set.getVA(iContentVAID).size() / iSamplesPerTexture);

		THeatMap = new Texture[iNrSelBar];

		int iTextureHeight = set.getVA(iContentVAID).size();
		int iTextureWidth = set.getVA(iStorageVAID).size();

		float fLookupValue = 0;
		float fOpacity = 0;

		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4
				/ iNrSelBar);

		int iCount = 0;
		int iTextureCounter = 0;

		for (Integer iContentIndex : set.getVA(iContentVAID))
		{
			iCount++;
			for (Integer iStorageIndex : set.getVA(iStorageVAID))
			{
				if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER,
						iContentIndex)
						|| contentSelectionManager.checkStatus(ESelectionType.SELECTION,
								iContentIndex) || detailLevel.compareTo(EDetailLevel.LOW) > 0)
					fOpacity = 1.0f;
				else
					fOpacity = 0.3f;

				fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
						iContentIndex);

				float[] fArMappingColor = colorMapper.getColor(fLookupValue);

				float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
						fArMappingColor[2], fOpacity };

				FbTemp.put(fArRgba);
			}
			if (iCount >= (iTextureHeight / iNrSelBar))
			{
				FbTemp.rewind();

				TextureData texData = new TextureData(GL.GL_RGBA /* internalFormat */, set
						.getVA(iStorageVAID).size() /* height */, set.getVA(iContentVAID)
						.size()
						/ iNrSelBar /* width */, 0 /* border */, GL.GL_RGBA /* pixelFormat */,
						GL.GL_FLOAT /* pixelType */, false /* mipmap */,
						false /* dataIsCompressed */, false /* mustFlipVertically */, FbTemp, null);

				THeatMap[iTextureCounter] = TextureIO.newTexture(0);
				THeatMap[iTextureCounter].updateImage(texData);

				iAlNumberSamples.add(iCount);

				iTextureCounter++;
				iCount = 0;
			}
		}
	}

	/**
	 * Create embedded heatmap, register heatmap as a sender and receiver
	 * 
	 * @param 
	 */
	private void createHeatMap()
	{
		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) generalManager
				.getCommandManager().createCommandByType(ECommandType.CREATE_GL_HEAT_MAP_3D);

		ArrayList<Integer> alSetIDs = new ArrayList<Integer>();

		for (ISet set : alSets)
		{
			alSetIDs.add(set.getID());
		}
		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0,
				fHeatMapWidth, -20, 20, alSetIDs, -1);

		cmdView.doCommand();

		glHeatMapView = (GLHeatMap) cmdView.getCreatedObject();

		// // Register heatmap as sender to event mediator
		// ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
		// arMediatorIDs.add(glHeatMapView.getID());

		generalManager.getEventPublisher().addSender(EMediatorType.HIERACHICAL_HEAT_MAP,
				glHeatMapView);
		generalManager.getEventPublisher().addReceiver(EMediatorType.HIERACHICAL_HEAT_MAP,
				glHeatMapView);

		generalManager.getEventPublisher().addSender(EMediatorType.HIERACHICAL_HEAT_MAP, this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.HIERACHICAL_HEAT_MAP,
				this);
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
	public synchronized void displayRemote(GL gl)
	{
		if (set == null)
			return;

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

	/**
	 * Function called any time a update is triggered by embedded heatmap
	 * 
	 * @param
	 */
	protected void reactOnExternalSelection()
	{
		int iIndex = 0;
		int iTexture = 0;
		int iPos = 0;
		HeatMapSelection temp;

		AlSelection.clear();

		Set<Integer> setMouseOverElements = contentSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);

		for (Integer iSelectedID : setMouseOverElements)
		{
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTexture = (int) Math.floor(iIndex / iAlNumberSamples.get(0));
			iPos = iIndex - (iTexture * iAlNumberSamples.get(0));

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(),
					ESelectionType.MOUSE_OVER);
			AlSelection.add(temp);
		}
	}

	/**
	 * Render caption, simplified version used in (original) heatmap
	 * 
	 * @param gl
	 * @param sLabel
	 * @param fXOrigin
	 * @param fYOrigin
	 * @param fFontScaling
	 */
	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin,
			float fFontScaling)
	{
		textRenderer.setColor(1, 1, 1, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, 0);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, 0, 0, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glTranslatef(-fXOrigin, -fYOrigin, 0);
		gl.glPopAttrib();
	}

	/**
	 * Render a curved (nice looking) connection line from given start point to
	 * given end point
	 * 
	 * @param gl
	 * @param startpoint
	 * @param endpoint
	 */
	private void renderCurvedConnectionLine(GL gl, Vec3f startpoint, Vec3f endpoint)
	{
		Spline3D spline;
		float accuracy = 0.1f;
		float margin = 0.1f;
		float fxDiff = endpoint.x() - startpoint.x();

		Vec3f startspline = new Vec3f(startpoint.x() + fxDiff / 4, startpoint.y(), startpoint
				.z());
		Vec3f endspline = new Vec3f(endpoint.x() - fxDiff / 4, endpoint.y(), endpoint.z());

		Vec3f[] vecs = new Vec3f[4];

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(startpoint.x(), startpoint.y(), startpoint.z());
		gl.glVertex3f(startpoint.x() + fxDiff / 4, startpoint.y(), startpoint.z());
		gl.glEnd();

		vecs[0] = startspline;
		vecs[1] = new Vec3f(startspline.x() + (endspline.x() - startspline.x()) / 5 * 2f,
				startspline.y() + (endspline.y() - startspline.y()) / 5 * 1f, startspline.z());
		vecs[2] = new Vec3f(startspline.x() + (endspline.x() - startspline.x()) / 5 * 3f,
				startspline.y() + (endspline.y() - startspline.y()) / 5 * 4f, startspline.z());
		vecs[3] = endspline;

		spline = new Spline3D(vecs, accuracy, margin);

		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < 100 * 2; i++)
		{
			Vec3f vec = spline.getPositionAt((float) i / 100 * 2);
			gl.glVertex3f(vec.x(), vec.y(), vec.z());
		}
		gl.glEnd();

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(endpoint.x() - fxDiff / 4, endpoint.y(), endpoint.z());
		gl.glVertex3f(endpoint.x(), endpoint.y(), endpoint.z());
		gl.glEnd();
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

	/**
	 * Render the first stage of the hierarchy (OverviewBar)
	 * 
	 * @param gl
	 */
	private void renderOverviewBar(GL gl)
	{
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;

		fHeight = viewFrustum.getHeight();
		fWidth = 0.1f;

		float fStep = fHeight / iNrSelBar;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < iNrSelBar; i++)
		{
			THeatMap[iNrSelBar - i - 1].enable();
			THeatMap[iNrSelBar - i - 1].bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			TextureCoords texCoords = THeatMap[iNrSelBar - i - 1].getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, iNrSelBar - i));
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(0, fyOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, fyOffset + fStep, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fWidth, fyOffset + fStep, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(fWidth, fyOffset, 0);
			gl.glEnd();
			gl.glPopName();

			fyOffset += fStep;
			THeatMap[iNrSelBar - i - 1].disable();
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of the currently (in stage
	 * 2) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerOverviewBar(final GL gl)
	{
		float fHeight = viewFrustum.getHeight();
		float fStep = fHeight / iNrSelBar;
		float fFieldWith = 0.1f;
		Vec3f startpoint;
		Vec3f endpoint;

		gl.glColor4f(1f, 1f, 0f, 1f);

		gl.glLineWidth(2f);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar), 0);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar), 0);
		gl.glEnd();

		gl.glColor4f(0f, 0f, 0f, 0.4f);

		startpoint = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		endpoint = new Vec3f(GAP_LEVEL1_2, fHeight, 0);
		renderCurvedConnectionLine(gl, startpoint, endpoint);

		startpoint = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar), 0);
		endpoint = new Vec3f(GAP_LEVEL1_2, 0, 0);
		renderCurvedConnectionLine(gl, endpoint, startpoint);

		float foffsetPick = ((fStep * (iNrSelBar - iSelectorBar + 1)) - (fStep * (iNrSelBar - iSelectorBar)))
				/ iAlNumberSamples.get(iSelectorBar - 1);

		gl.glColor4f(1f, 1f, 0f, 1f);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0,
				fStep * (iNrSelBar - iSelectorBar + 1) - (iFirstSample * foffsetPick), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1)
				- (iFirstSample * foffsetPick), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1)
				- ((iLastSample + 1) * foffsetPick), 0);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar + 1)
				- ((iLastSample + 1) * foffsetPick), 0);
		gl.glEnd();

		gl.glColor4f(0f, 0f, 0f, 0.4f);

		startpoint = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1)
				- iFirstSample * foffsetPick, 0);
		endpoint = new Vec3f(GAP_LEVEL1_2, fPosCursorFirstElement, 0);
		renderCurvedConnectionLine(gl, startpoint, endpoint);

		startpoint = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1)
				- (iLastSample + 1) * foffsetPick, 0);
		endpoint = new Vec3f(GAP_LEVEL1_2, fPosCursorLastElement, 0);
		renderCurvedConnectionLine(gl, startpoint, endpoint);

		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
	}

	/**
	 * Render marker next to OverviewBar for visualization of selected elements
	 * in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsOverviewBar(GL gl)
	{
		float fHeight = viewFrustum.getHeight();
		float fStep = fHeight / iNrSelBar;
		float fBarWidth = 0.1f;

		gl.glColor4f(1f, 0f, 0f, 1f);

		for (HeatMapSelection selection : AlSelection)
		{
			// elements in overview bar
			if (iSelectorBar != (selection.getTexture() + 1))
			{
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(fBarWidth, fStep
						* (iNrSelBar - (selection.getTexture() + 1) + 1), 0);
				gl.glVertex3f(fBarWidth + 0.05f, fStep
						* (iNrSelBar - (selection.getTexture() + 1) + 1), 0);
				gl.glVertex3f(fBarWidth + 0.05f, fStep
						* (iNrSelBar - (selection.getTexture() + 1)), 0);
				gl
						.glVertex3f(fBarWidth, fStep
								* (iNrSelBar - (selection.getTexture() + 1)), 0);
				gl.glEnd();
			}
		}
		gl.glColor4f(1f, 1f, 0f, 1f);
	}

	/**
	 * Render the second stage of the hierarchy (Texture)
	 * 
	 * @param gl
	 */
	private void renderTextureHeatMap(GL gl)
	{
		float fHeight;
		float fWidth;

		THeatMap[iSelectorBar - 1].enable();
		THeatMap[iSelectorBar - 1].bind();

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

		TextureCoords texCoords = THeatMap[iSelectorBar - 1].getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth() / 4.0f;

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_FIELD_SELECTION, 1));

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2d(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, fHeight, 0);
		gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, fHeight, 0);
		gl.glTexCoord2d(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0);
		gl.glEnd();

		gl.glPopName();

		gl.glPopAttrib();

		THeatMap[iSelectorBar - 1].disable();

	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3)
	 * rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerTexture(final GL gl)
	{
		float fFieldWith = viewFrustum.getWidth() / 4.0f;
		float fHeightSample = viewFrustum.getHeight() / iAlNumberSamples.get(iSelectorBar - 1);
		Vec3f startpoint;
		Vec3f endpoint;

		gl.glColor4f(1f, 1f, 0f, 1f);
		gl.glLineWidth(2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, viewFrustum.getHeight() - (iFirstSample * fHeightSample), 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - (iFirstSample * fHeightSample), 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight()
				- ((iLastSample + 1) * fHeightSample), 0);
		gl.glVertex3f(0, viewFrustum.getHeight() - ((iLastSample + 1) * fHeightSample), 0);
		gl.glEnd();

		if (bIsDraggingActive == false)
		{
			fPosCursorFirstElement = viewFrustum.getHeight() - (iFirstSample * fHeightSample);
			fPosCursorLastElement = viewFrustum.getHeight()
					- ((iLastSample + 1) * fHeightSample);
		}

		gl.glColor4f(0f, 0f, 0f, 0.4f);

		startpoint = new Vec3f(fFieldWith, viewFrustum.getHeight()
				- (iFirstSample * fHeightSample), 0);
		if (bIsHeatmapInFocus)
			endpoint = new Vec3f(fFieldWith + (GAP_LEVEL2_3 + 0.35f) * 5f, viewFrustum
					.getHeight() - 0.1f, 0);
		else
			endpoint = new Vec3f(fFieldWith + GAP_LEVEL2_3 + 0.35f,
					viewFrustum.getHeight() - 0.1f, 0);
		renderCurvedConnectionLine(gl, startpoint, endpoint);

		startpoint = new Vec3f(fFieldWith, viewFrustum.getHeight()
				- ((iLastSample + 1) * fHeightSample), 0);
		if (bIsHeatmapInFocus)
			endpoint = new Vec3f(fFieldWith + (GAP_LEVEL2_3 + 0.35f) * 5f, 0.5f, 0);
		else
			endpoint = new Vec3f(fFieldWith + GAP_LEVEL2_3 + 0.35f, 0.5f, 0);
		renderCurvedConnectionLine(gl, startpoint, endpoint);

		if (bRenderCaption == true)
		{
			renderCaption(gl, "Number Samples:" + iSamplesPerHeatmap, 0.0f, viewFrustum
					.getHeight()
					- (iPickedSample * fHeightSample), 0.01f);
			bRenderCaption = false;
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of selected elements in
	 * the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsTexture(GL gl)
	{
		float fFieldWith = viewFrustum.getWidth() / 4.0f;
		float fHeightSample = viewFrustum.getHeight() / iAlNumberSamples.get(iSelectorBar - 1);

		gl.glColor4f(1f, 0f, 0f, 1f);

		for (HeatMapSelection selection : AlSelection)
		{
			// elements in texture
			if (iSelectorBar == (selection.getTexture() + 1))
			{
				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(0, viewFrustum.getHeight()
						- ((selection.getPos() - 1) * fHeightSample), 0);
				gl.glVertex3f(fFieldWith, viewFrustum.getHeight()
						- ((selection.getPos() - 1) * fHeightSample), 0);
				gl.glVertex3f(fFieldWith, viewFrustum.getHeight()
						- (selection.getPos() * fHeightSample), 0);
				gl.glVertex3f(0, viewFrustum.getHeight()
						- (selection.getPos() * fHeightSample), 0);
				gl.glEnd();
			}
		}
	}

	/**
	 * Render cursor used for controlling hierarchical heatmap (e.g. next
	 * Texture, previous Texture, set heatmap in focus)
	 * 
	 * @param gl
	 */
	private void renderCursor(final GL gl)
	{
		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth() / 4.0f;

		Texture tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		
		if (iSelectorBar != 1)
		{
			// Polygon for selecting previous texture
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 1));
			// left			
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0.0f, fHeight, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.1f, fHeight, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.1f, fHeight + 0.1f, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0.0f, fHeight + 0.1f, 0);
			gl.glEnd();
			// right
			if (bIsHeatmapInFocus)
			{
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth/5 - 0.1f, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth/5, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth/5, fHeight + 0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth/5 - 0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
			}
			else
			{
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth - 0.1f, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth, fHeight + 0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth - 0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
			}
			gl.glPopName();
		}

		if (iSelectorBar != iNrSelBar)
		{
			// Polygon for selecting next texture
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 2));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0.0f, 0.0f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fWidth, 0.0f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fWidth, -0.1f, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0.0f, -0.1f, 0);
			gl.glEnd();
			gl.glPopName();
		}

//		if (set.getVA(iStorageVAID).size() > MAX_NUM_SAMPLES && bIsHeatmapInFocus == false)
//		{
//			// Polygon for setting heatMap in focus
//			gl.glPushName(pickingManager.getPickingID(iUniqueID,
//					EPickingType.HIER_HEAT_MAP_INFOCUS_SELECTION, 1));
//			gl.glBegin(GL.GL_QUADS);
//			gl.glTexCoord2f(texCoords.left(), texCoords.top());
//			gl.glVertex3f(fWidth, fPosCursorFirstElement, 0);
//			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
//			gl.glVertex3f(fWidth + 0.2f, fPosCursorFirstElement, 0);
//			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
//			gl.glVertex3f(fWidth + 0.2f, fPosCursorLastElement, 0);
//			gl.glTexCoord2f(texCoords.right(), texCoords.top());
//			gl.glVertex3f(fWidth, fPosCursorLastElement, 0);
//			gl.glEnd();
//
//			gl.glPopName();
//		}		
		
		tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.NAVIGATION_NEXT_SMALL);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();
		
		if (bIsHeatmapInFocus == false)
		{
			gl.glColor4f(0f, 0f, 0f, 0.7f);
			// Polygon for iFirstElement-Cursor
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_CURSOR, 1));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.0f, fPosCursorFirstElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorFirstElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorFirstElement + 0.1f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.0f, fPosCursorFirstElement + 0.1f, 0);
			gl.glEnd();
			gl.glPopName();

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorLastElement, 0);
			gl.glVertex3f(0.0f, fPosCursorLastElement, 0);
			gl.glVertex3f(0.0f, fPosCursorFirstElement, 0);
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorFirstElement, 0);
			gl.glEnd();
			
			// Polygon for iLastElement-Cursor
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_CURSOR, 2));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.0f, fPosCursorLastElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorLastElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(-GAP_LEVEL1_2/4, fPosCursorLastElement - 0.1f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.0f, fPosCursorLastElement - 0.1f, 0);
			gl.glEnd();
			gl.glPopName();
		}

		gl.glPopAttrib();
		tempTexture.disable();
	}

	@Override
	public synchronized void display(GL gl)
	{
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// GLHelperFunctions.drawAxis(gl);
		if (bIsDraggingActive)
		{
			handleCursorDragging(gl);
			if (pickingTriggerMouseAdapter.wasMouseReleased())
				bIsDraggingActive = false;
		}

		gl.glCallList(iGLDisplayListToCall);
		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		float fright = 0.0f;
		float ftop = viewFrustum.getTop() - 0.2f;

		float fleftOffset = 0;

		// render embedded heat map
		if (bIsHeatmapInFocus)
		{
			fright = viewFrustum.getWidth() - 1.3f;
			fleftOffset = 0.1f + // width level 1
					GAP_LEVEL1_2 + // width gap between level 1 and 2
					(viewFrustum.getWidth() / 4f) * 0.2f + // width level 2
					GAP_LEVEL2_3; // width gap between level 2 and 3
			gl.glTranslatef(fleftOffset, 0, 0);
		}
		else
		{
			fright = viewFrustum.getWidth() - 3.0f;
			fleftOffset = 0.1f + // width level 1
					GAP_LEVEL1_2 + // width gap between level 1 and 2
					viewFrustum.getWidth() / 4f + // width level 2
					GAP_LEVEL2_3;// width gap between level 2 and 3
			gl.glTranslatef(fleftOffset, 0, 0);
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_VIEW_SELECTION, glHeatMapView.getID()));

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		if (bIsHeatmapInFocus)
		{
			gl.glTranslatef(-1.0f, 0, 0);
		}
		else
		{
			gl.glTranslatef(-3.0f, 0, 0);
		}
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex)
	{

		if (bHasFrustumChanged)
		{
			glHeatMapView.setDisplayListDirty();
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentSelectionManager.getNumberOfElements() == 0)
		{
			renderSymbol(gl);
		}
		else
		{
			handleTexturePicking(gl);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			viewFrustum.setTop(viewFrustum.getTop() - 0.2f);
			gl.glTranslatef(0, 0.1f, 0);

			// all stuff for rendering level 1 (overview bar)
			renderOverviewBar(gl);
			renderMarkerOverviewBar(gl);
			renderSelectedElementsOverviewBar(gl);

			gl.glTranslatef(GAP_LEVEL1_2, 0, 0);

			if (bIsHeatmapInFocus)
			{
				fAnimationScale = 0.2f;
			}
			else
			{
				fAnimationScale = 1.0f;
			}

			gl.glScalef(fAnimationScale, 1, 1);

			// all stuff for rendering level 2 (textures)
			renderTextureHeatMap(gl);
			renderMarkerTexture(gl);
			renderSelectedElementsTexture(gl);
			
			gl.glScalef(1 / fAnimationScale, 1, 1);
			
			renderCursor(gl);

			viewFrustum.setTop(viewFrustum.getTop() + 0.2f);
			gl.glTranslatef(0, -0.1f, 0);

			gl.glTranslatef(-GAP_LEVEL1_2, 0, 0);

			gl.glDisable(GL.GL_STENCIL_TEST);

		}
		gl.glEndList();
	}

	/**
	 * Function responsible for handling SelectionDelta for embedded heatmap
	 * 
	 * @param
	 */
	private void triggerSelectionBlock()
	{
		int iCount = (iAlNumberSamples.get(0) * (iSelectorBar - 1)) + iFirstSample;

		ArrayList<SelectionCommand> alSelectionCommand = new ArrayList<SelectionCommand>();
		alSelectionCommand.add(new SelectionCommand(ESelectionCommandType.RESET));

		IVirtualArrayDelta delta = new VirtualArrayDelta(EIDType.EXPRESSION_INDEX);

		IVirtualArray currentVirtualArray = set.getVA(iContentVAID);
		int iIndex = 0;

		int iContentIndex = 0;

		for (int index = 0; index < iSamplesPerHeatmap; index++)
		{
			iIndex = iCount + index;
			iContentIndex = currentVirtualArray.get(iIndex);

			delta.add(VADeltaItem.append(iContentIndex));
			// //set elements selected in embedded heatMap
			// for (HeatMapSelection selection : AlSelection)
			// {
			// // System.out.println("selection.getContentIndex(): " +
			// // selection.getContentIndex());
			// // System.out.println("iContentIndex: "+ iContentIndex);
			//						
			// if (selection.getContentIndex() == iContentIndex)
			// delta.addSelection(iContentIndex,
			// ESelectionType.MOUSE_OVER);//selection.getSelectionType());
			// }
		}

		generalManager.getEventPublisher().triggerVAUpdate(
				EMediatorType.HIERACHICAL_HEAT_MAP, this, delta, alSelectionCommand);
	}

	public synchronized void renderHorizontally(boolean bRenderStorageHorizontally)
	{

		if (glHeatMapView.isInDefaultOrientation())
			glHeatMapView.changeOrientation(false);
		else
			glHeatMapView.changeOrientation(true);

		// this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
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
		// TODO probably remove this here
		// renderStyle.initFieldSizes();

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public String getShortInfo()
	{
		return "Hierarchical Heat Map (" + set.getVA(iContentVAID).size() + " genes / "
				+ set.getVA(iStorageVAID).size() + " experiments)";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Hierarchical Heat Map\n");

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

	/**
	 * Determine selected element in stage 2 (texture)
	 * 
	 * @param gl
	 */
	private void handleTexturePicking(GL gl)
	{
		int iNumberSample = iAlNumberSamples.get(iSelectorBar - 1);
		float fOffsety;
		float fHeightSample = viewFrustum.getHeight() / iNumberSample;
		float[] fArPickingCoords = new float[3];

		
		if (PickingPoint != null)
		{
			fArPickingCoords = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(
					gl, PickingPoint.x, PickingPoint.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1];
			iPickedSample = (int) Math.ceil(fOffsety / fHeightSample);
			PickingPoint = null;

			if ((iSamplesPerHeatmap % 2) == 0)
			{
				iFirstSample = iPickedSample - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
			}
			else
			{
				iFirstSample = iPickedSample - (int) Math.ceil(iSamplesPerHeatmap / 2);
				iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
			}

			if (iPickedSample < iSamplesPerHeatmap / 2)
			{
				iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
				iFirstSample = 0;
				iLastSample = iSamplesPerHeatmap - 1;
			}
			else if (iPickedSample > (iNumberSample - 1 - iSamplesPerHeatmap / 2))
			{
				iPickedSample = (int) Math.ceil(iNumberSample
						- iSamplesPerHeatmap / 2);
				iLastSample = iNumberSample - 1;
				iFirstSample = iNumberSample - iSamplesPerHeatmap;
			}
		}
	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleCursorDragging(final GL gl)
	{
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float fselElement;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fHeight = viewFrustum.getHeight() - 0.2f;
		float fStep = fHeight / iAlNumberSamples.get(0);
		float fPosCursor;
		
		// cursor for iFirstElement
		if (iDraggedCursor == 1)
		{
			fPosCursor = fArTargetWorldCoordinates[1] - 0.1f;
			// =
			// offset
			if (fPosCursor > fPosCursorLastElement && iFirstSample >= 0)
			{
				fPosCursorFirstElement = fPosCursor;
				fselElement = (viewFrustum.getHeight() - 0.1f - fArTargetWorldCoordinates[1])
						/ fStep;
				if ((int) Math.floor(fselElement) >= 0)
				{
					iFirstSample = (int) Math.floor(fselElement);
					iSamplesPerHeatmap = iLastSample - iFirstSample + 1;
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursor == 2)
		{
			fPosCursor = fArTargetWorldCoordinates[1] - 0.1f;
			// =
			// offset
			if (fPosCursorFirstElement > fPosCursor
					&& iLastSample <= iAlNumberSamples.get(0));
			{
				fPosCursorLastElement = fPosCursor;
				fselElement = (viewFrustum.getHeight() - 0.1f - fArTargetWorldCoordinates[1])
						/ fStep;
				if ((int) Math.ceil(fselElement) < iAlNumberSamples.get(0));
				{
					iLastSample = (int) Math.ceil(fselElement);
					iSamplesPerHeatmap = iLastSample - iFirstSample + 1;
				}
			}
		}

		// update Preference store
		generalManager.getPreferenceStore().setValue(
				PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, iSamplesPerHeatmap);
		
		setDisplayListDirty();
		triggerSelectionBlock();

		if (pickingTriggerMouseAdapter.wasMouseReleased())
		{
			bIsDraggingActive = false;
		}
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

		switch (ePickingType)
		{
			case HIER_HEAT_MAP_TEXTURE_CURSOR:
				switch (pickingMode)
				{
					case CLICKED:

						if (iExternalID == 1)
						{
							if (iSelectorBar != 1)
							{
								iSelectorBar--;
								initPosCursor();
								triggerSelectionBlock();
								setDisplayListDirty();
							}
						}
						if (iExternalID == 2)
						{
							if (iSelectorBar != iNrSelBar)
							{
								iSelectorBar++;
								initPosCursor();
								triggerSelectionBlock();
								setDisplayListDirty();
							}
						}

						setDisplayListDirty();
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_CURSOR:
				switch (pickingMode)
				{
					case CLICKED:

						bRenderCaption = true;
						setDisplayListDirty();
						break;

					case DRAGGED:

						bRenderCaption = true;
						bIsDraggingActive = true;
						iDraggedCursor = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						bRenderCaption = true;
						setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
//			case HIER_HEAT_MAP_INFOCUS_SELECTION:
//				switch (pickingMode)
//				{
//					case CLICKED:
//
//						bIsHeatmapInFocus = true;
//						glHeatMapView.setDisplayListDirty();
//						setDisplayListDirty();
//						break;
//
//					case MOUSE_OVER:
//						break;
//				}
//				pickingManager.flushHits(iUniqueID, ePickingType);
//				break;
			case HIER_HEAT_MAP_TEXTURE_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:

						iSelectorBar = iExternalID;
						initPosCursor();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						iSelectorBar = iExternalID;
						initPosCursor();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:

						bIsHeatmapInFocus = false;
						PickingPoint = pick.getPickedPoint();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						PickingPoint = pick.getPickedPoint();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case HIER_HEAT_MAP_VIEW_SELECTION:
				switch (pickingMode)
				{
					case MOUSE_OVER:

						// System.out.println("Selected external heat map mouse over");

						break;

					case CLICKED:

						bIsHeatmapInFocus = true;
						glHeatMapView.setDisplayListDirty();
						setDisplayListDirty();

						break;

					case DRAGGED:
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.HIER_HEAT_MAP_VIEW_SELECTION);

				break;
		}
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta)
	{
		// renderStyle.updateFieldSizes();
		// fAlXDistances.clear();
		// float fDistance = 0;
		//
		// for (Integer iStorageIndex : set.getVA(iContentVAID))
		// {
		// fAlXDistances.add(fDistance);
		// if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER,
		// iStorageIndex)
		// || contentSelectionManager.checkStatus(ESelectionType.SELECTION,
		// iStorageIndex))
		// // if(selectionDelta.)
		// {
		// fDistance += renderStyle.getSelectedFieldWidth();
		// }
		// else
		// {
		// fDistance += renderStyle.getNormalFieldWidth();
		// }
		// // contentSelectionManager.addToType(ESelectionType.SELECTION,
		// // iStorageIndex);
		//
		// }
		// super.handleConnectedElementRep(selectionDelta);
	}

	@Override
	protected SelectedElementRep createElementRep(EIDType idType, int iStorageIndex)
			throws InvalidAttributeValueException
	{
		SelectedElementRep elementRep;// = new SelectedElementRep(iUniqueID,
		// 0.0f, 0.0f, 0.0f);

		int iContentIndex = set.getVA(iContentVAID).indexOf(iStorageIndex);
		if (iContentIndex == -1)
		{
			// TODO this shouldn't happen
			generalManager.getLogger().log(Level.SEVERE,
					"No element in virtual array for storage index");

			return null;
		}
		// renderStyle.resetFieldWidths();
		// Vec2f vecFieldWithAndHeight =
		// renderStyle.getFieldWidthAndHeight(iContentIndex);

		// for (int iCount = 0; iCount <= iContentIndex; iCount++)
		// {
		// vecFieldWithAndHeight = renderStyle.getFieldWidthAndHeight(iCount);
		// }

		float fXValue = fAlXDistances.get(iContentIndex) + renderStyle.getSelectedFieldWidth()
				/ 2;// + renderStyle.getXSpacing();

		float fYValue = renderStyle.getYCenter();// + vecFieldWithAndHeight.y()
		// * set.getVA(iContentVAID).size() / 2;

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

	@Override
	protected void rePosition(int iElementID)
	{
		int iSelection;
		if (bRenderStorageHorizontally)
			iSelection = iContentVAID;
		else
			iSelection = iStorageVAID;

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

	@Override
	public void renderContext(boolean bRenderOnlyContext)
	{
		throw new IllegalStateException(
				"Rendering only context not supported for the hierachical heat map");
		// this.bRenderOnlyContext = bRenderOnlyContext;
		//
		// if (this.bRenderOnlyContext)
		// iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		// else
		// {
		// if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
		// initCompleteList();
		//
		// iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		// }
		//
		// contentSelectionManager.setVA(set.getVA(iContentVAID));
		// renderStyle.setActiveVirtualArray(iContentVAID);
		//
		// setDisplayListDirty();

	}

	@Override
	public void broadcastElements()
	{
		ISelectionDelta delta = contentSelectionManager.getCompleteDelta();
		triggerSelectionUpdate(EMediatorType.HIERACHICAL_HEAT_MAP, delta, null);
		setDisplayListDirty();
	}

	@Override
	public void resetSelections()
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

	public void changeFocus(boolean bInFocus)
	{
		bIsHeatmapInFocus = (bIsHeatmapInFocus == true) ? false : true;

		setDisplayListDirty();
	}

	public boolean isInFocus()
	{
		return bIsHeatmapInFocus;
	}

	@Override
	public void triggerVAUpdate(EMediatorType mediatorType, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand)
	{
		// TODO Auto-generated method stub
		
	}

}
