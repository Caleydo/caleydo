package org.geneview.core.view.opengl.canvas.pathway;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Color;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IPathwayItemManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.util.slerp.SlerpMod;
import org.geneview.core.util.sound.SoundPlayer;
import org.geneview.core.util.system.SystemTime;
import org.geneview.core.util.system.Time;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.util.GLDragAndDrop;
import org.geneview.core.view.opengl.util.GLInfoAreaRenderer;
import org.geneview.core.view.opengl.util.GLPathwayMemoPad;
import org.geneview.core.view.opengl.util.GLTextUtils;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

import com.sun.opengl.util.BufferUtil;

/**
 * Jukebox setup that supports slerp animation.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class GLCanvasJukeboxPathway3D 
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {

	public static final int MAX_LOADED_PATHWAYS = 600;
	public static final int PATHWAY_TEXTURE_PICKING_ID_RANGE_START = 620;
	public static final int FREE_PICKING_ID_RANGE_START = 1200;
	public static final String TICK_SOUND = "resources/sounds/tick.wav";
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 2.3f;
	private static final float SCALING_FACTOR_LAYERED_LAYER = 1f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.1f;

	private float fTextureTransparency = 1.0f;
	private float fLastMouseMovedTimeStamp = 0;
	
	private boolean bRebuildVisiblePathwayDisplayLists = false;
	private boolean bIsMouseOverPickingEvent = false;
	private boolean bEnablePathwayTextures = true;
	private boolean bMouseOverMemoPad = false;
	private boolean bSelectionChanged = false;
	private boolean bUpdateReceived = false;

	private int iMouseOverPickedPathwayId = -1;

	private GLPathwayManager refGLPathwayManager;

	private GLPathwayTextureManager refGLPathwayTextureManager;

	private PickingJoglMouseListener pickingTriggerMouseAdapter;

	private ArrayList<SlerpAction> arSlerpActions;

	/**
	 * Slerp factor 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	private JukeboxHierarchyLayer pathwayUnderInteractionLayer; // contains only

	// one pathway

	private JukeboxHierarchyLayer pathwayLayeredLayer;

	private JukeboxHierarchyLayer pathwayPoolLayer;
	
//	private ArrayList<float[]> alTextureColorByLayerPos;

	private PathwayVertexGraphItemRep selectedVertex;

	private GLInfoAreaRenderer infoAreaRenderer;

	private HashMap<Integer, Integer> refHashPoolLinePickId2PathwayId;

	/**
	 * Hash map stores which pathways contain the currently selected vertex and
	 * how often this vertex is contained.
	 */
	private HashMap<Integer, Integer> refHashPathwayContainingSelectedVertex2VertexCount;

	private GLPathwayMemoPad memoPad;

	private GLDragAndDrop dragAndDrop;
	
	private Time time;
	
	private int iLazyPathwayLoadingId = -1;

	/**
	 * Constructor
	 * 
	 */
	public GLCanvasJukeboxPathway3D(final IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, null, iViewId, iParentContainerId, "");

		this.refViewCamera.setCaller(this);

		// refHashPathwayIdToModelMatrix = new HashMap<Integer, Mat4f>();
		refGLPathwayManager = new GLPathwayManager(refGeneralManager);
		refGLPathwayTextureManager = new GLPathwayTextureManager(
				refGeneralManager);
		arSlerpActions = new ArrayList<SlerpAction>();

		refHashPoolLinePickId2PathwayId = new HashMap<Integer, Integer>();
		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();

		// Create Jukebox hierarchy
		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer(1, 
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				refGLPathwayTextureManager);
		pathwayLayeredLayer = new JukeboxHierarchyLayer(4, 
				SCALING_FACTOR_LAYERED_LAYER, refGLPathwayTextureManager);
		pathwayPoolLayer = new JukeboxHierarchyLayer(MAX_LOADED_PATHWAYS, 
				SCALING_FACTOR_POOL_LAYER, refGLPathwayTextureManager);
		pathwayUnderInteractionLayer.setParentLayer(pathwayLayeredLayer);
		pathwayLayeredLayer.setChildLayer(pathwayUnderInteractionLayer);
		pathwayLayeredLayer.setParentLayer(pathwayPoolLayer);
		pathwayPoolLayer.setChildLayer(pathwayLayeredLayer);

		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.7f, -1.4f, 0f));
		transformPathwayUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));
		pathwayUnderInteractionLayer.setTransformByPositionIndex(0,
				transformPathwayUnderInteraction);

		pickingTriggerMouseAdapter = (PickingJoglMouseListener) openGLCanvasDirector
				.getJoglCanvasForwarder().getJoglMouseListener();

		infoAreaRenderer = new GLInfoAreaRenderer(refGeneralManager,
				refGLPathwayManager);
		infoAreaRenderer.enableColorMappingArea(true);
		
		memoPad = new GLPathwayMemoPad(refGLPathwayManager,
				refGLPathwayTextureManager);

		dragAndDrop = new GLDragAndDrop(refGLPathwayTextureManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.view.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas(GL gl) {

	    time = new SystemTime();
	    ((SystemTime) time).rebase();
		
		// Clearing window and set background to WHITE
		// is already set inside JoglCanvasForwarder
		//gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL.GL_ALPHA_TEST);
		// gl.glAlphaFunc(GL.GL_GREATER, 0);

		// gl.glEnable(GL.GL_TEXTURE_2D);
		// gl.glTexEnvf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE,
		// GL.GL_REPLACE);

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		// gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);

		memoPad.init(gl);
		initPathwayData(gl);

		setInitGLDone();
	}

	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, alSetSelection);
		buildPathwayPool(gl);
		buildLayeredPathways(gl);
	}

	public void renderPart(GL gl) {

//		if (bRebuildVisiblePathwayDisplayLists)
//			rebuildVisiblePathwayDisplayLists(gl);
		
		if (iLazyPathwayLoadingId != -1)
		{
			loadPathwayToUnderInteractionPosition(iLazyPathwayLoadingId);
			iLazyPathwayLoadingId = -1;
		}
		
		handlePicking(gl);
		
		if (arSlerpActions.isEmpty() && bSelectionChanged 
				&& selectedVertex != null)
		{
			bSelectionChanged = false;
			bRebuildVisiblePathwayDisplayLists = true;	
		}
		
		if (bRebuildVisiblePathwayDisplayLists)
			rebuildVisiblePathwayDisplayLists(gl);
		
		if (dragAndDrop.isDragActionRunning()) {
			dragAndDrop.renderDragThumbnailTexture(gl);
		}
		
		if (bUpdateReceived)
		{
			// unpack selection item reps
			int[] iArSelectionId = alSetSelection.get(0).getSelectionIdArray();
			ArrayList<IGraphItem> alSelectedVertexReps = new ArrayList<IGraphItem>();
			IPathwayItemManager pathwayItemManager = refGeneralManager.getSingelton().getPathwayItemManager();
			for (int iItemIndex = 0; iItemIndex < iArSelectionId.length; iItemIndex++)
			{
				alSelectedVertexReps.add(
						(IGraphItem) pathwayItemManager.getItem(iArSelectionId[iItemIndex]));
			}
			loadDependentPathways(gl, alSelectedVertexReps);
			
			bUpdateReceived = false;
		}
		
		time.update();

		doSlerpActions(gl);
		
		renderScene(gl);
		renderInfoArea(gl);

		renderConnectingLines(gl);
		
		// int viewport[] = new int[4];
		// gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		//
		// GLProjectionUtils.orthogonalStart(gl, 1, 1);
		//		
		// gl.glColor3f(1,0,0);
		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex2f(0, 0);
		// gl.glVertex2f(0, 1);
		// gl.glVertex2f(1, 1);
		// gl.glVertex2f(1, 0);
		// gl.glEnd();
		//		
		// GLProjectionUtils.orthogonalEnd(gl);

	}

	public void renderScene(final GL gl) {
		
		renderPathwayPool(gl);
		renderPathwayLayered(gl);
		renderPathwayUnderInteraction(gl);
		
//		renderPathwayPoolWithPathwayLayeredConnection(gl);

		memoPad.renderMemoPad(gl);
	}

	private void buildLayeredPathways(final GL gl) {

		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = 1.1f;
		int iMaxLayers = 4;

		// Create free pathway layer spots
		Transform transform;
		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2.7f, fLayerYPos, 0f));
			transform.setScale(new Vec3f(SCALING_FACTOR_LAYERED_LAYER,
					SCALING_FACTOR_LAYERED_LAYER,
					SCALING_FACTOR_LAYERED_LAYER));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
			pathwayLayeredLayer.setTransformByPositionIndex(iLayerIndex,
					transform);

			fLayerYPos -= 1f;
		}
	}

	private void buildPathwayPool(final GL gl) {

		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		int iMaxLines = MAX_LOADED_PATHWAYS;

		// Create free pathway spots
		Transform transform;
		for (int iLineIndex = 0; iLineIndex < iMaxLines; iLineIndex++)
		{
			transform = new Transform();
			transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad));
			// transform.setTranslation(new Vec3f(-4.0f, -iLineIndex *
			// fLineHeight, 10));
			transform.setScale(new Vec3f(SCALING_FACTOR_POOL_LAYER,
					SCALING_FACTOR_POOL_LAYER,
					SCALING_FACTOR_POOL_LAYER));
			pathwayPoolLayer.setTransformByPositionIndex(iLineIndex, transform);
		}
		
		// Load KEGG pathways
		refGeneralManager.getSingelton().getPathwayManager()
			.loadAllPathwaysByType(EPathwayDatabaseType.KEGG);

		// Load BioCarta pathways
		refGeneralManager.getSingelton().getPathwayManager()
			.loadAllPathwaysByType(EPathwayDatabaseType.BIOCARTA);
		
		Iterator<IGraph> iterPathwayGraphs = refGeneralManager.getSingelton()
			.getPathwayManager().getRootPathway().getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN).iterator();

		while(iterPathwayGraphs.hasNext())
		{
			pathwayPoolLayer.addElement((iterPathwayGraphs.next()).getId());
		}
	}

	private void renderPathwayUnderInteraction(final GL gl) {

		// Check if a pathway is currently under interaction
		if (pathwayUnderInteractionLayer.getElementList().size() == 0)
			return;

		int iPathwayId = pathwayUnderInteractionLayer.getElementIdByPositionIndex(0);
		renderPathwayById(gl, iPathwayId, pathwayUnderInteractionLayer);
	}

	private void renderPathwayLayered(final GL gl) {

		Iterator<Integer> iterPathwayElementList = pathwayLayeredLayer.getElementList().iterator();
		int iPathwayId = 0;
		
		while(iterPathwayElementList.hasNext())
		{		
			iPathwayId = iterPathwayElementList.next();		
			
			// Check if pathway is visible
			if(!pathwayLayeredLayer.getElementVisibilityById(iPathwayId))
				continue;
						
			renderPathwayById(gl, iPathwayId, pathwayLayeredLayer);			
		}
	}
	
	private void renderPathwayById(final GL gl,
			final int iPathwayId, 
			final JukeboxHierarchyLayer layer) {
		
		// Check if pathway is visible
		if(!layer.getElementVisibilityById(iPathwayId))
			return;
		
		gl.glPushMatrix();
		
//		// ORIGIN
//		drawAxis(gl);
		
		Transform transform = layer.getTransformByElementId(iPathwayId);
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();		
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z() );

//		drawAxis(gl);
		
		if (bEnablePathwayTextures)
		{
			if (!layer.getElementList().isEmpty()
					&& pathwayUnderInteractionLayer.getElementIdByPositionIndex(0) == iPathwayId)
			{
				refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
						fTextureTransparency, true);
			}
			else
			{
				refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
						fTextureTransparency, false);
			}
		}
		
		float tmp = refGLPathwayTextureManager.getTextureByPathwayId(
				iPathwayId).getImageHeight()* GLPathwayManager.SCALING_FACTOR_Y;
		
		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);
		
		if (layer.equals(pathwayLayeredLayer))
		{
			refGLPathwayManager.renderPathway(gl, iPathwayId, false);
		}
		else
		{
			refGLPathwayManager.renderPathway(gl, iPathwayId, true);
		}
		
		gl.glTranslatef(0, -tmp, 0);
		
		gl.glPopMatrix();
		
//		// TEST
//		Vec3f vecSrc = new Vec3f(0,0,0);
//		Vec3f vecDest = new Vec3f(1,0,0);
//		Vec3f vecDest2 = new Vec3f(1,1,0);
//		
//		gl.glLineWidth(5);
//		gl.glColor3f(1,1,0);
//		gl.glBegin(GL.GL_LINE_LOOP);
//		gl.glVertex3f(vecSrc.x(), vecSrc.y(), vecSrc.z());
//		gl.glColor3f(1,0,0);
//		gl.glVertex3f(vecDest.x(), vecDest.y(), vecDest.z());
//		gl.glColor3f(0,0,1);
//		gl.glVertex3f(vecDest2.x(), vecDest2.y(), vecDest2.z());		
//		gl.glEnd();
//		
//		Mat4f_GeneraRotfScale mat = new Mat4f_GeneraRotfScale(translation, scale, rot);
//		Vec3f vecSrcTrans = mat.xformPt(vecSrc);
//		Vec3f vecDestTrans = mat.xformPt(vecDest);
//		Vec3f vecDest2Trans = mat.xformPt(vecDest2);
//				
//		vecSrcTrans.add(translation);
//		vecDestTrans.add(translation);
//		vecDest2Trans.add(translation);
//		
//		vecSrcTrans.componentMul(scale);
//		vecDestTrans.componentMul(scale);
//		vecDest2Trans.componentMul(scale);
//		
//		gl.glBegin(GL.GL_LINE_LOOP);
//		gl.glColor3f(1,1,0);
//		gl.glVertex3f(vecSrcTrans.x(), vecSrcTrans.y(), vecSrcTrans.z());
//		gl.glColor3f(1,0,0);
//		gl.glVertex3f(vecDestTrans.x(), vecDestTrans.y(), vecDestTrans.z());
//		gl.glColor3f(0,0,1);
//		gl.glVertex3f(vecDest2Trans.x(), vecDest2Trans.y(), vecDest2Trans.z());		
//		gl.glEnd();
		
	}

	private void renderPathwayPool(final GL gl) {

		// Initialize magnification factors with 0 (minimized)
		ArrayList<Integer> alMagnificationFactor = new ArrayList<Integer>();

		for (int iPathwayIndex = 0; iPathwayIndex < MAX_LOADED_PATHWAYS; iPathwayIndex++)
		{
			alMagnificationFactor.add(0);
		}

		// Load pathway storage
		int iPathwayId = 0;
		for (int iPathwayIndex = 0; iPathwayIndex < pathwayPoolLayer.getElementList().size(); iPathwayIndex++)
		{
			iPathwayId = pathwayPoolLayer.getElementIdByPositionIndex(iPathwayIndex);
		
			if (iMouseOverPickedPathwayId == iPathwayId)
			{
				if ((iPathwayIndex - 2 >= 0)
						&& (alMagnificationFactor.get(iPathwayIndex - 2) < 1))
				{
					alMagnificationFactor.set(iPathwayIndex - 2, 1);
				}

				if ((iPathwayIndex - 1 >= 0)
						&& (alMagnificationFactor.get(iPathwayIndex - 1) < 2))
				{
					alMagnificationFactor.set(iPathwayIndex - 1, 2);
				}

				alMagnificationFactor.set(iPathwayIndex, 3);

				if ((iPathwayIndex + 1 < alMagnificationFactor.size())
						&& (alMagnificationFactor.get(iPathwayIndex + 1) < 2))
				{
					alMagnificationFactor.set(iPathwayIndex + 1, 2);
				}

				if ((iPathwayIndex + 2 < alMagnificationFactor.size())
						&& (alMagnificationFactor.get(iPathwayIndex + 2) < 1))
				{
					alMagnificationFactor.set(iPathwayIndex + 2, 1);
				}
			} else if (pathwayLayeredLayer.containsElement(iPathwayId)
					|| pathwayUnderInteractionLayer.containsElement(iPathwayId))
			{
				alMagnificationFactor.set(iPathwayIndex, 2);
			} else if (refHashPathwayContainingSelectedVertex2VertexCount
					.containsKey(iPathwayId))
			{
				alMagnificationFactor.set(iPathwayIndex, 1);
			}
		}

		recalculatePathwayPoolTransformation(alMagnificationFactor);

		String sRenderText = "";
		float fYPos = 0;
		float fZPos = 8;

		for (int iPathwayIndex = 0; iPathwayIndex < pathwayPoolLayer.getElementList().size(); iPathwayIndex++)
		{		
			gl.glPushMatrix();

			iPathwayId = pathwayPoolLayer.getElementIdByPositionIndex(iPathwayIndex);

			gl.glLoadName(iPathwayIndex + 1);

			if (!refHashPoolLinePickId2PathwayId.containsKey(iPathwayIndex + 1))
			{
				refHashPoolLinePickId2PathwayId.put(iPathwayIndex + 1, iPathwayId);
			}

			Transform transform = pathwayPoolLayer
					.getTransformByElementId(iPathwayId);
			Vec3f translation = transform.getTranslation();
			gl.glTranslatef(translation.x(), translation.y(), translation.z()
					+ fZPos);

			// Append identical vertex count to pathway title
			if (alMagnificationFactor.get(iPathwayIndex) != 0)
			{
				sRenderText = ((PathwayGraph) refGeneralManager.getSingelton()
						.getPathwayManager().getItem(iPathwayId)).getTitle();
			}
			
			if (refHashPathwayContainingSelectedVertex2VertexCount.containsKey(iPathwayId))
			{	
				sRenderText = sRenderText
						+ " - "
						+ refHashPathwayContainingSelectedVertex2VertexCount
								.get(iPathwayId).toString();
			}
			
			if (pathwayUnderInteractionLayer.containsElement(iPathwayId))
				gl.glColor4f(1, 0, 0, 1);
			else
				gl.glColor4f(0, 0, 0, 1);
			
			if (alMagnificationFactor.get(iPathwayIndex) == 3)
			{
				GLTextUtils.renderText(gl, sRenderText, 18, 0, 0.06f, 0);
				fYPos = 0.15f;
			} else if (alMagnificationFactor.get(iPathwayIndex) == 2)
			{
				GLTextUtils.renderText(gl, sRenderText, 12, 0, 0.04f, 0);
				fYPos = 0.1f;
			} else if (alMagnificationFactor.get(iPathwayIndex) == 1)
			{
				GLTextUtils.renderText(gl, sRenderText, 10, 0, 0.02f, 0);
				fYPos = 0.07f;
			} else if (alMagnificationFactor.get(iPathwayIndex) == 0)
			{
				fYPos = 0.02f;

				gl.glColor3f(0, 0, 0);

				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(0, 0, 0);
				gl.glVertex3f(0, fYPos, 0);
				gl.glVertex3f(0.1f, fYPos, 0);
				gl.glVertex3f(0.1f, 0, 0);
				gl.glEnd();
			}

			gl.glColor4f(0, 0, 0, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(0, fYPos, 0.1f);
			gl.glVertex3f(0.5f, fYPos, 0.1f);
			gl.glVertex3f(0.5f, 0, 0.1f);
			gl.glEnd();

			gl.glPopMatrix();
		}
	}

	private void recalculatePathwayPoolTransformation(
			final ArrayList<Integer> alMagnificationFactor) {

		Transform transform;
		float fPathwayPoolHeight = -2.0f;
		
		for (int iLineIndex = 0; iLineIndex < pathwayPoolLayer.getElementList().size(); iLineIndex++)
		{
			transform = pathwayPoolLayer
					.getTransformByPositionIndex(iLineIndex);

			if (alMagnificationFactor.get(iLineIndex) == 3)
			{
				fPathwayPoolHeight += 0.15;
			} else if (alMagnificationFactor.get(iLineIndex) == 2)
			{
				fPathwayPoolHeight += 0.1;
			} else if (alMagnificationFactor.get(iLineIndex) == 1)
			{
				fPathwayPoolHeight += 0.07;
			} else if (alMagnificationFactor.get(iLineIndex) == 0)
			{
				fPathwayPoolHeight += 0.01;
			}

			transform.setTranslation(new Vec3f(-4.0f, -fPathwayPoolHeight, -6));
		}
	}
	
//	private void renderPathwayPoolWithPathwayLayeredConnection(final GL gl) {
//		
//		Iterator<Integer> iterPathwayLayered = 
//			pathwayLayeredLayer.getElementList().iterator();
//	
//		Vec3f vecMatSrc = new Vec3f(0, 0, 0);
//		Vec3f vecMatDest = new Vec3f(0, 0, 0);
//		Vec3f vecTransformedSrc = new Vec3f(0, 0, 0);
//		Vec3f vecTransformedDest = new Vec3f(0, 0, 0);
//		
//		Vec3f vecTranslationSrc;
//		Vec3f vecTranslationDest;
//		Vec3f vecScaleSrc;
//		Vec3f vecScaleDest;
//		Rotf rotSrc;
//		Rotf rotDest;
//		Mat4f matSrc = new Mat4f();
//		Mat4f matDest = new Mat4f();
//		matSrc.makeIdent();
//		matDest.makeIdent();
//		
//		while(iterPathwayLayered.hasNext()) {
//			
//			int iElementId = iterPathwayLayered.next();
//
//			vecTranslationSrc = pathwayLayeredLayer.getTransformByElementId(iElementId).getTranslation();
//			vecScaleSrc = pathwayLayeredLayer.getTransformByElementId(iElementId).getScale();
////			rotSrc = pathwayLayeredLayer.getTransformByElementId(iElementId).getRotation();
//
//			vecTranslationDest = pathwayPoolLayer.getTransformByElementId(iElementId).getTranslation();
//			vecScaleDest = pathwayPoolLayer.getTransformByElementId(iElementId).getScale();
////			rotDest = pathwayLayeredLayer.getTransformByElementId(iElementId).getRotation();
//			
////			vecMatSrc.set(tmpVertexGraphItemRepSrc.getXPosition() * GLPathwayManager.SCALING_FACTOR_X,
////					 (refGLPathwayTextureManager.getTextureByPathwayId(iPathwayIdSrc)
////						.getImageHeight()-tmpVertexGraphItemRepSrc.getYPosition()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
////			
////			vecMatDest.set(tmpVertexGraphItemRepDest.getXPosition() * GLPathwayManager.SCALING_FACTOR_X,
////					 (refGLPathwayTextureManager.getTextureByPathwayId(iPathwayIdDest)
////					 	.getImageHeight()-tmpVertexGraphItemRepDest.getYPosition()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
////
////			rotSrc.toMatrix(matSrc);
////			rotDest.toMatrix(matDest);
////
////			matSrc.xformPt(vecMatSrc, vecTransformedSrc);
////			matDest.xformPt(vecMatDest, vecTransformedDest);
//			
//			vecTransformedSrc.add(vecTranslationSrc);
//			vecTransformedSrc.componentMul(vecScaleSrc);
//			vecTransformedDest.add(vecTranslationDest);
//			vecTransformedDest.componentMul(vecScaleDest);
//			
//			gl.glBegin(GL.GL_LINES);
//			gl.glVertex3f(vecTransformedSrc.x(),
//					vecTransformedSrc.y(),
//					vecTransformedSrc.z());
//			gl.glVertex3f(vecTransformedDest.x(),
//					vecTransformedDest.y(),
//					vecTransformedDest.z());
//			gl.glEnd();		
//		}
//	}

	private void renderInfoArea(final GL gl) {

		if (selectedVertex != null
				&& !bMouseOverMemoPad
				&& infoAreaRenderer.isPositionValid())
		{
			infoAreaRenderer.renderInfoArea(gl, selectedVertex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.geneview.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);

		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		int[] iArOptional = refSetSelection.getOptionalDataArray();
		if (iArOptional.length != 0)
		{
			iLazyPathwayLoadingId = 
				refSetSelection.getOptionalDataArray()[0];
		}
		
		int[] iArSelectionId = refSetSelection.getSelectionIdArray();
		if (iArSelectionId.length != 0)
		{
			selectedVertex = (PathwayVertexGraphItemRep) refGeneralManager.getSingelton()
				.getPathwayItemManager().getItem(iArSelectionId[0]);
			
			bRebuildVisiblePathwayDisplayLists = true;
//			bSelectionChanged = true;
			infoAreaRenderer.resetPoint();
			
			refGLPathwayManager.updateSelectionSet(
					(SetSelection) refSetSelection);
			
			bUpdateReceived = true;
		}
		
		refSetSelection.returnReadToken();	
	}

	public void updateReceiver(Object eventTrigger) {

	}

	private void doSlerpActions(final GL gl) {

		if (arSlerpActions.isEmpty())
			return;
				
		if (iSlerpFactor < 1000)
		{
			// Makes animation rendering speed independent
			iSlerpFactor += 1400 * time.deltaT();
			
			if (iSlerpFactor > 1000)
				iSlerpFactor = 1000;
		}
		
		slerpPathway(gl, arSlerpActions.get(0));
		// selectedVertex = null;
	}

	private void slerpPathway(final GL gl, SlerpAction slerpAction) {

		int iPathwayId = slerpAction.getElementId();
		SlerpMod slerpMod = new SlerpMod();
		
		if (slerpAction.getDestinationHierarchyLayer().equals(pathwayLayeredLayer))
		{	
			refGLPathwayTextureManager.loadPathwayTextureById(iPathwayId);
		}
		
		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getOriginPosIndex()), slerpAction
				.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()),
				(int)iSlerpFactor / 1000f);

		gl.glPushMatrix();
		
		slerpMod.applySlerp(gl, transform);
		
		refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
				fTextureTransparency, true);

		float tmp = refGLPathwayTextureManager
				.getTextureByPathwayId(iPathwayId).getImageHeight()
				* GLPathwayManager.SCALING_FACTOR_Y;
		gl.glTranslatef(0, tmp, 0);
		refGLPathwayManager.renderPathway(gl, iPathwayId, false);
		gl.glTranslatef(0, -tmp, 0);

		gl.glPopMatrix();

		if (iSlerpFactor >= 1000)
		{
			slerpAction.getDestinationHierarchyLayer()
					.setElementVisibilityById(true, iPathwayId);

			arSlerpActions.remove(slerpAction);
			iSlerpFactor = 0;
		}

		if ((iSlerpFactor == 0))
			slerpMod.playSlerpSound();
	}

	private void handlePicking(final GL gl) {

		Point pickPoint = null;

		boolean bMouseReleased =
			pickingTriggerMouseAdapter.wasMouseReleased();
		
		if (pickingTriggerMouseAdapter.wasMousePressed()
				|| bMouseReleased)
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			bIsMouseOverPickingEvent = false;
		}

		if (pickingTriggerMouseAdapter.wasMouseMoved())
		{
			// Restart timer
			fLastMouseMovedTimeStamp = System.nanoTime();
			bIsMouseOverPickingEvent = true;
		} 
		else if (bIsMouseOverPickingEvent == true
				&& System.nanoTime() - fLastMouseMovedTimeStamp >= 0)// 1e9)
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			fLastMouseMovedTimeStamp = System.nanoTime();
		}
		else if (pickingTriggerMouseAdapter.wasMouseDragged())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
		}

		// Check if an object was picked
		if (pickPoint != null)
		{	
			pickObjects(gl, pickPoint);
			bIsMouseOverPickingEvent = false;
			dragAndDrop.setCurrentMousePos(gl, pickPoint);
		}
		
		// Check if a drag&drop action was performed to add a pathway to the memo pad
		if (bMouseReleased && bMouseOverMemoPad && dragAndDrop.isDragActionRunning())
		{
			if (dragAndDrop.getDraggedObjectedId() != -1)
				memoPad.addPathwayToMemoPad(dragAndDrop.getDraggedObjectedId());

			dragAndDrop.stopDragAction();
		}
		// Check if a drag&drop action was performed to replace the pathway under interaction
		else if (bMouseReleased && !bMouseOverMemoPad && dragAndDrop.isDragActionRunning())
		{
			loadPathwayToUnderInteractionPosition(dragAndDrop.getDraggedObjectedId());
			dragAndDrop.stopDragAction();
		}
		// Cancel drag&drop action if mouse isn't released over the memo pad area.
		else if (bMouseReleased && !bMouseOverMemoPad)
		{
			dragAndDrop.stopDragAction();
		}
	}

	private void pickObjects(final GL gl, Point pickPoint) {

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glPushName(0);

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 1.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -4*h, 4*h, 1.0f, 1000.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		renderScene(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint);
	}

	protected void processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], final Point pickPoint) {

		// System.out.println("Number of hits: " +iHitCount);

		int iPtr = 0;
		int i = 0;

		int iPickedObjectId = 0;

		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		for (i = 0; i < iHitCount; i++)
		{
			iPtr++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPtr] < iMinimumZValue)
			{
				iMinimumZValue = iArPickingBuffer[iPtr];
				iPtr++;
				iPtr++;
				iPickedObjectId = iArPickingBuffer[iPtr];
			}
			iPtr++;
		}
		
		if (iPickedObjectId == 0)
		{
			// Remove pathway pool fisheye
			iMouseOverPickedPathwayId = -1;

			infoAreaRenderer.resetPoint();

			return;
		}

		// Do not handle picking if a slerp action is in progress
		if (!arSlerpActions.isEmpty())
			return;

		bMouseOverMemoPad = false;
		
		//System.out.println("Pick ID: " + iPickedObjectId);

		// Check if picked object a non-pathway object (like pathway pool lines,
		// navigation handles, etc.)
		if (iPickedObjectId < MAX_LOADED_PATHWAYS)
		{
			int iPathwayId = refHashPoolLinePickId2PathwayId
					.get(iPickedObjectId);

			// If mouse over event - just highlight pathway line
			if (bIsMouseOverPickingEvent)
			{
				// Check if mouse moved to another pathway in the pathway pool
				// list
				if (iMouseOverPickedPathwayId != iPathwayId)
				{
					iMouseOverPickedPathwayId = iPathwayId;
					playPathwayPoolTickSound();
				}

				return;
			}

			loadPathwayToUnderInteractionPosition(iPathwayId);
			
			return;
		} 
		else if (iPickedObjectId >= PATHWAY_TEXTURE_PICKING_ID_RANGE_START 
				&& iPickedObjectId < FREE_PICKING_ID_RANGE_START)
		{
			if (bIsMouseOverPickingEvent)
				return;
			
			if (dragAndDrop.isDragActionRunning())
			{
				iMouseOverPickedPathwayId = 
					refGLPathwayTextureManager.getPathwayIdByPathwayTexturePickingId(iPickedObjectId);
			}
			else
			{
				dragAndDrop.startDragAction(
						refGLPathwayTextureManager.getPathwayIdByPathwayTexturePickingId(iPickedObjectId));
			}
			
			return;
		}

		if (iPickedObjectId == GLPathwayMemoPad.MEMO_PAD_PICKING_ID)
		{
			bMouseOverMemoPad = true;
		} 
		else if (iPickedObjectId == GLPathwayMemoPad.MEMO_PAD_TRASH_CAN_PICKING_ID)
		{
			// Remove dragged object from memo pad
			memoPad.removePathwayFromMemoPad(
					dragAndDrop.getDraggedObjectedId());
			
			dragAndDrop.stopDragAction();
		}

		PathwayVertexGraphItemRep pickedVertexRep
			= refGLPathwayManager.getVertexRepByPickID(iPickedObjectId);

		if (pickedVertexRep == null)
			return;

		if (selectedVertex != null
				&& !selectedVertex.equals(pickedVertexRep))
		{
			loadNodeInformationInBrowser(((PathwayVertexGraphItem)pickedVertexRep.getAllItemsByProp(
					EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
			
			infoAreaRenderer.resetAnimation();
		}
		
		// Remove pathway pool fisheye
		iMouseOverPickedPathwayId = -1;

		// System.out.println("Picked node:" +refPickedVertexRep.getName());

		// Reset pick point
		infoAreaRenderer.convertWindowCoordinatesToWorldCoordinates(gl,
				pickPoint.x, pickPoint.y);

		// If event is just mouse over (and not real picking)
		// highlight the object under the cursor
		if (bIsMouseOverPickingEvent)
		{
			if (selectedVertex == null ||
					(selectedVertex != null && !selectedVertex.equals(pickedVertexRep)))
			{
				selectedVertex = pickedVertexRep;
				bSelectionChanged = true;
			}
			
			return;
		}
		
//		// If event is just mouse over (and not real picking)
//		// highlight the object under the cursor
//		if (bIsMouseOverPickingEvent)
//		{
//			if (selectedVertex != null
//					&& !selectedVertex.equals(pickedVertexRep))
//			{
//				infoAreaRenderer.resetAnimation();
//			}
//
//			selectedVertex = pickedVertexRep;
//			bSelectionChanged = true;
//			
//			return;
//		}

		if (pickedVertexRep.getPathwayVertexGraphItem().getType().equals(
				EPathwayVertexType.map))
		{
			String strTmp = pickedVertexRep.getPathwayVertexGraphItem().getName();

			int iPathwayId = -1;
			try
			{
				iPathwayId = Integer.parseInt(strTmp
						.substring(strTmp.length() - 4));
			} catch (NumberFormatException e)
			{
				return;
			}

			loadPathwayToUnderInteractionPosition(iPathwayId);

			return;
		}
		else if (pickedVertexRep.getPathwayVertexGraphItem().getType()
				.equals(EPathwayVertexType.enzyme) 
			|| pickedVertexRep.getPathwayVertexGraphItem().getType()
				.equals(EPathwayVertexType.gene)
			|| pickedVertexRep.getPathwayVertexGraphItem().getType()
			.equals(EPathwayVertexType.other)) // FIXME: just for testing BioCarta integration
		{
			selectedVertex = pickedVertexRep;
			bSelectionChanged = true;

			loadDependentPathwayBySingleVertex(gl, selectedVertex);
		}
	}

	private void loadPathwayToUnderInteractionPosition(final int iPathwayId) {

		refGeneralManager
				.getSingelton()
				.logMsg(this.getClass().getSimpleName()
								+ ": loadPathwayToUnderInteractionPosition(): Pathway with ID "
								+ iPathwayId + " is under interaction.",
						LoggerType.VERBOSE);

		// Check if pathway is already under interaction
		if (pathwayUnderInteractionLayer.containsElement(iPathwayId))
			return;
		
		loadNodeInformationInBrowser(((PathwayGraph)refGeneralManager
				.getSingelton().getPathwayManager().getItem(iPathwayId)).getExternalLink());
		
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < 1000)
			return;

		arSlerpActions.clear();

		// Check if selected pathway is loaded.
		if (!refGeneralManager.getSingelton().getPathwayManager().hasItem(iPathwayId))
		{
			return;
		}

		// Slerp current pathway back to layered view
		if (!pathwayUnderInteractionLayer.getElementList().isEmpty())
		{
			SlerpAction reverseSlerpAction = new SlerpAction(
					pathwayUnderInteractionLayer.getElementIdByPositionIndex(0),
					pathwayUnderInteractionLayer, true);

			arSlerpActions.add(reverseSlerpAction);
		}

		SlerpAction slerpAction;

		// Prevent slerp action if pathway is already in layered view
		if (!pathwayLayeredLayer.containsElement(iPathwayId))
		{
			// Slerp to layered pathway view
			slerpAction = new SlerpAction(iPathwayId, pathwayPoolLayer, false);

			arSlerpActions.add(slerpAction);
		}

		// Slerp from layered to under interaction position
		slerpAction = new SlerpAction(iPathwayId, pathwayLayeredLayer, false);

		arSlerpActions.add(slerpAction);
		iSlerpFactor = 0;

		bRebuildVisiblePathwayDisplayLists = true;
		//selectedVertex = null;

		// Trigger update with current pathway that dependent pathways
		// know which pathway is currently under interaction
		int[] iArOptional = new int[1];
		iArOptional[0] = iPathwayId;
		alSetSelection.get(0).updateSelectionSet(iUniqueId, new int[0],
				new int[0], iArOptional);
	}

	@SuppressWarnings("unchecked")
	public LinkedList<Integer> getVisiblePathways() {

		LinkedList<Integer> tmpVisiblePathways = (LinkedList<Integer>) pathwayLayeredLayer
				.getElementList().clone();

		// Add pathway under interaction to pathways in layered view
		if (!pathwayUnderInteractionLayer.getElementList().isEmpty()
				&& !tmpVisiblePathways.contains(pathwayUnderInteractionLayer
						.getElementIdByPositionIndex(0)))
		{
			tmpVisiblePathways.add(pathwayUnderInteractionLayer
					.getElementIdByPositionIndex(0));
		}

		return tmpVisiblePathways;
	}

	public void loadDependentPathwayBySingleVertex(final GL gl,
			final PathwayVertexGraphItemRep vertex) {
		
		Iterator<IGraphItem> iterVertexGraphItems = 
			vertex.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).iterator();

		ArrayList<IGraphItem> alSelectedVertexGraphItemReps = 
			new ArrayList<IGraphItem>();
		
		// Remove duplicates by adding to a hash list
		HashSet<IGraphItem> set = new HashSet<IGraphItem>();
		
		while(iterVertexGraphItems.hasNext())
		{
			set.addAll(iterVertexGraphItems.next().getAllItemsByProp(
					EGraphItemProperty.ALIAS_CHILD));
		}
		
		alSelectedVertexGraphItemReps.addAll(set);
				
		loadDependentPathways(gl, alSelectedVertexGraphItemReps);
	}
		
	public void loadDependentPathways(final GL gl,
			final List<IGraphItem> alVertexRep) {

		// Remove pathways from stacked layer view
		pathwayLayeredLayer.removeAllElements();
		
		refHashPathwayContainingSelectedVertex2VertexCount.clear();
		
		Iterator<IGraphItem> iterIdenticalPathwayGraphItemReps = 
			alVertexRep.iterator();
		
		IGraphItem identicalPathwayGraphItemRep;
		int iPathwayId = 0;
		int iMaxPathwayCount = 0;
		
		while (iterIdenticalPathwayGraphItemReps.hasNext())
		{
			identicalPathwayGraphItemRep = iterIdenticalPathwayGraphItemReps.next();
	
			iPathwayId = ((PathwayGraph)identicalPathwayGraphItemRep
					.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0]).getKeggId();

			// Prevent slerp action if pathway is already in layered view
			if (!pathwayLayeredLayer.containsElement(iPathwayId))
			{
				if (iMaxPathwayCount < pathwayLayeredLayer
						.getCapacity())
				{
					iMaxPathwayCount++;
	
					// Slerp to layered pathway view
					SlerpAction slerpAction = new SlerpAction(iPathwayId,
							pathwayPoolLayer, false);
	
					arSlerpActions.add(slerpAction);
					iSlerpFactor = 0;
				}
			}

			// Check if pathway has already a vertex counted
			if (refHashPathwayContainingSelectedVertex2VertexCount.containsKey(iPathwayId))
			{
				// Increase current stored identical vertex count by 1
				refHashPathwayContainingSelectedVertex2VertexCount.put(
						iPathwayId, refHashPathwayContainingSelectedVertex2VertexCount.get(iPathwayId) + 1);
			}
			else
			{
				refHashPathwayContainingSelectedVertex2VertexCount.put(iPathwayId, 1);
			}
		}
		
		bRebuildVisiblePathwayDisplayLists = true;
	}

	private void rebuildVisiblePathwayDisplayLists(final GL gl) {

		// Reset rebuild trigger flag
		bRebuildVisiblePathwayDisplayLists = false;
		
		refGLPathwayManager.clearOldPickingIDs();

		if (selectedVertex != null)
		{
			// Write currently selected vertex to selection set
			int[] iArTmpSelectionId = new int[1];
			int[] iArTmpDepth = new int[1];
			iArTmpSelectionId[0] = selectedVertex.getId();
			iArTmpDepth[0] = 0;
			alSetSelection.get(0).getWriteToken();
			alSetSelection.get(0).updateSelectionSet(iUniqueId, iArTmpSelectionId, iArTmpDepth, new int[0]);
			alSetSelection.get(0).returnWriteToken();
		}
			
		refGLPathwayManager.performIdenticalNodeHighlighting();
		
		// Update display list if something changed
		// Rebuild display lists for visible pathways in layered view
		Iterator<Integer> iterVisiblePathway = pathwayLayeredLayer
				.getElementList().iterator();

		while (iterVisiblePathway.hasNext())
		{
			refGLPathwayManager.buildPathwayDisplayList(gl, iterVisiblePathway.next());
		}

		// Rebuild display lists for visible pathways in focus position
		if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
				&& !pathwayLayeredLayer.containsElement(pathwayUnderInteractionLayer
				.getElementIdByPositionIndex(0)))
		{
			refGLPathwayManager.buildPathwayDisplayList(gl, pathwayUnderInteractionLayer
							.getElementIdByPositionIndex(0));
		}
		
		// Cleanup unused textures
		refGLPathwayTextureManager.unloadUnusedTextures(getVisiblePathways());

		// Trigger update on current selection
		//alSetSelection.get(0).updateSelectionSet(iUniqueId);
	}

	private void playPathwayPoolTickSound() {

		SoundPlayer.playSoundByFilename(TICK_SOUND);
	}

	public void loadNodeInformationInBrowser(String sUrl) {

		if (sUrl.isEmpty())
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) refGeneralManager
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}

	/**
	 * @param textureTransparency
	 *            the fTextureTransparency to set
	 */
	public final void setTextureTransparency(float textureTransparency) {

		if ((textureTransparency >= 0.0f) && (textureTransparency <= 1.0f))
		{
			fTextureTransparency = textureTransparency;
			return;
		}

		refGeneralManager.getSingelton().logMsg(
				"setTextureTransparency() failed! value=" + textureTransparency
						+ " was out of range [0.0f .. 1.0f]",
				LoggerType.MINOR_ERROR);
	}
	
	// TODO: render connecting lines to display list
	public void renderConnectingLines(final GL gl) {
		
		if (!arSlerpActions.isEmpty() 
				|| pathwayLayeredLayer.getElementList().size() < 2 
				|| selectedVertex == null)
		{
			return;
		}
		
		Iterator<IGraphItem> iterSelectedVertexGraphItems = 
			selectedVertex.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).iterator();

		ArrayList<IGraphItem> alIdenticalVertexGraphItemReps = new ArrayList<IGraphItem>();
		
		while(iterSelectedVertexGraphItems.hasNext()) 
		{
			alIdenticalVertexGraphItemReps.addAll(
					iterSelectedVertexGraphItems.next().getAllItemsByProp(
						EGraphItemProperty.ALIAS_CHILD));
		}
		
		Iterator<IGraphItem> iterIdenticalVertexGraphItemReps;
		Iterator<IGraphItem> iterIdenticalVertexGraphItemRepsInnerLoop;
		PathwayVertexGraphItemRep tmpVertexGraphItemRepSrc;
		PathwayVertexGraphItemRep tmpVertexGraphItemRepDest;
		int iPathwayIdSrc = 0;
		int iPathwayIdDest = 0;
	
		Vec3f vecMatSrc = new Vec3f(0, 0, 0);
		Vec3f vecMatDest = new Vec3f(0, 0, 0);
		Vec3f vecTransformedSrc = new Vec3f(0, 0, 0);
		Vec3f vecTransformedDest = new Vec3f(0, 0, 0);
		
		Vec3f vecTranslationSrc;
		Vec3f vecTranslationDest;
		Vec3f vecScaleSrc;
		Vec3f vecScaleDest;
		Rotf rotSrc;
		Rotf rotDest;
		Mat4f matSrc = new Mat4f();
		Mat4f matDest = new Mat4f();
		matSrc.makeIdent();
		matDest.makeIdent();
		
		gl.glLineWidth(4);
		Color tmpLineColor = new PathwayRenderStyle().getLayerConnectionLinesColor();
		gl.glColor4f(tmpLineColor.getRed() / 255.0f, 
				tmpLineColor.getGreen() / 255.0f, 
				tmpLineColor.getBlue() / 255.0f, 1.0f);
		
		
		for (int iLayerIndex = 0; iLayerIndex < 
			pathwayLayeredLayer.getElementList().size() - 1; iLayerIndex++)	
		{
			vecTranslationSrc = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex).getTranslation();
			vecScaleSrc = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex).getScale();
			rotSrc = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex).getRotation();

			vecTranslationDest = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex+1).getTranslation();
			vecScaleDest = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex+1).getScale();
			rotDest = pathwayLayeredLayer.getTransformByPositionIndex(iLayerIndex+1).getRotation();
				
			iPathwayIdSrc = pathwayLayeredLayer.getElementIdByPositionIndex(iLayerIndex);
			iPathwayIdDest = pathwayLayeredLayer.getElementIdByPositionIndex(iLayerIndex+1);
			
			iterIdenticalVertexGraphItemReps = 
				alIdenticalVertexGraphItemReps.iterator();
			
			while(iterIdenticalVertexGraphItemReps.hasNext())
			{
				tmpVertexGraphItemRepSrc =
					(PathwayVertexGraphItemRep) iterIdenticalVertexGraphItemReps.next();
				
				// Leave loop if parent pathway of the graph item is not the current one
				if (pathwayLayeredLayer.getElementIdByPositionIndex(iLayerIndex) == 
					((PathwayGraph)tmpVertexGraphItemRepSrc.getAllGraphByType(
							EGraphItemHierarchy.GRAPH_PARENT).get(0)).getKeggId())
				{							
					iterIdenticalVertexGraphItemRepsInnerLoop = 
						alIdenticalVertexGraphItemReps.iterator();
					
					while (iterIdenticalVertexGraphItemRepsInnerLoop.hasNext())
					{
						tmpVertexGraphItemRepDest =
							(PathwayVertexGraphItemRep) iterIdenticalVertexGraphItemRepsInnerLoop.next();
						
						// Leave loop if parent pathway of the graph item is not the current one
						if (pathwayLayeredLayer.getElementIdByPositionIndex(iLayerIndex+1) == 
							((PathwayGraph)tmpVertexGraphItemRepDest.getAllGraphByType(
									EGraphItemHierarchy.GRAPH_PARENT).get(0)).getKeggId())
						{		
							vecMatSrc.set(tmpVertexGraphItemRepSrc.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X,
									 (refGLPathwayTextureManager.getTextureByPathwayId(iPathwayIdSrc)
										.getImageHeight()-tmpVertexGraphItemRepSrc.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
							
							vecMatDest.set(tmpVertexGraphItemRepDest.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X,
									 (refGLPathwayTextureManager.getTextureByPathwayId(iPathwayIdDest)
									 	.getImageHeight()-tmpVertexGraphItemRepDest.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
	
							rotSrc.toMatrix(matSrc);
							rotDest.toMatrix(matDest);

							matSrc.xformPt(vecMatSrc, vecTransformedSrc);
							matDest.xformPt(vecMatDest, vecTransformedDest);
							

							vecTransformedSrc.componentMul(vecScaleSrc);
							vecTransformedSrc.add(vecTranslationSrc);
							vecTransformedDest.componentMul(vecScaleDest);
							vecTransformedDest.add(vecTranslationDest);
							
							gl.glBegin(GL.GL_LINES);
							gl.glVertex3f(vecTransformedSrc.x(),
									vecTransformedSrc.y(),
									vecTransformedSrc.z());
							gl.glVertex3f(vecTransformedDest.x(),
									vecTransformedDest.y(),
									vecTransformedDest.z());
							gl.glEnd();		
						}
					}
				}
			}	
		}
	}
	
	public void enableGeneMapping(final boolean bEnableMapping) {
		
		refGLPathwayManager.enableGeneMapping(bEnableMapping);
		bRebuildVisiblePathwayDisplayLists = true;
	}
	
	public void enablePathwayTextures(final boolean bEnablePathwayTextures) {
		
		refGLPathwayManager.enableEdgeRendering(!bEnablePathwayTextures);
		bRebuildVisiblePathwayDisplayLists = true;
		
		this.bEnablePathwayTextures = bEnablePathwayTextures;
	}
	
	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		
		bRebuildVisiblePathwayDisplayLists = true;
		refGLPathwayManager.enableNeighborhood(bEnableNeighborhood);
	}
	
	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		
		bRebuildVisiblePathwayDisplayLists = true;
		refGLPathwayManager.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}
	
	public void enableAnnotation(final boolean bEnableAnnotation) {
		
		refGLPathwayManager.enableAnnotation(bEnableAnnotation);
	}
	
	public void clearAllPathways() {
		
		pathwayLayeredLayer.removeAllElements();
		pathwayUnderInteractionLayer.removeAllElements();

		bRebuildVisiblePathwayDisplayLists = true;
		refHashPathwayContainingSelectedVertex2VertexCount.clear();
		
		// TODO: clear textures and other stuff
	}
	
	private void drawAxis(final GL gl) {
		
		gl.glLineWidth(10);
	    gl.glBegin(GL.GL_LINES);
	    gl.glColor4f(1, 0, 0, 1);
	    gl.glVertex3f(0,  0,  0);
	    gl.glVertex3f(1,  0,  0);
	    gl.glColor4f(0, 1, 0, 1);
	    gl.glVertex3f( 0,  0,  0);
	    gl.glVertex3f( 0, 1,  0);
	    gl.glColor4f(0, 0, 1, 1);
	    gl.glVertex3f( 0,  0,  0);
	    gl.glVertex3f( 0,  0, 1);
	    gl.glEnd();
	}
}