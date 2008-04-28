package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.graph.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.IPathwayItemManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.util.slerp.SlerpAction;
import org.caleydo.core.util.slerp.SlerpMod;
import org.caleydo.core.util.sound.SoundPlayer;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;
import org.caleydo.core.view.opengl.util.drag.GLDragAndDropPathway;
import org.caleydo.core.view.opengl.util.infoarea.GLInfoAreaRenderer;
import org.caleydo.core.view.opengl.util.memopad.GLPathwayMemoPad;
import org.caleydo.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.caleydo.core.view.opengl.util.selection.GenericSelectionManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Jukebox setup for pathways that supports slerp animation.
 * 
 * @author Marc Streit
 *
 * @deprecated Use GLCanvasJukebox3D instead
 * 
 */
public class GLCanvasJukeboxPathway3D 
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {

	public static final int PATHWAY_POOL_SELECTION = 3;
	
	public static final int MAX_LOADED_PATHWAYS = 600;
	public static final String TICK_SOUND = "resources/sounds/tick.wav";
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 2.3f;
	private static final float SCALING_FACTOR_LAYERED_LAYER = 1f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.1f;

	private float fTextureTransparency = 0.75f;
	
	private boolean bRebuildVisiblePathwayDisplayLists = false;
	private boolean bEnablePathwayTextures = true;
	private boolean bSelectionChanged = false;
	private boolean bUpdateReceived = false;

	private int iMouseOverPickedPathwayId = -1;

	private GLPathwayManager refGLPathwayManager;

	private GLPathwayTextureManager refGLPathwayTextureManager;
	
	private ArrayList<SlerpAction> arSlerpActions;

	/**
	 * Slerp factor 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	private JukeboxHierarchyLayer pathwayUnderInteractionLayer; 

	private JukeboxHierarchyLayer pathwayLayeredLayer;

	private JukeboxHierarchyLayer pathwayPoolLayer;

	private PathwayVertexGraphItemRep selectedVertex;

	private GLInfoAreaRenderer infoAreaRenderer;

	/**
	 * Hash map stores which pathways contain the currently selected vertex and
	 * how often this vertex is contained.
	 */
	private HashMap<Integer, Integer> refHashPathwayContainingSelectedVertex2VertexCount;

	private GLPathwayMemoPad memoPad;

	private GLDragAndDropPathway dragAndDrop;
	
	private Time time;
	
	private int iLazyPathwayLoadingId = -1;
	
	private TextRenderer textRenderer;
	
	private GenericSelectionManager pathwayVertexSelectionManager;
	
//	private Vec3f vecScaling;
//	private Vec3f vecTranslation;

	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasJukeboxPathway3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);

		refGLPathwayManager = new GLPathwayManager(generalManager);
		refGLPathwayTextureManager = new GLPathwayTextureManager(
				generalManager);
		arSlerpActions = new ArrayList<SlerpAction>();

		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();

		// Create Jukebox hierarchy
//		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer(generalManager,
//				1, SCALING_FACTOR_UNDER_INTERACTION_LAYER, refGLPathwayTextureManager);
//		pathwayLayeredLayer = new JukeboxHierarchyLayer(generalManager,
//				4, SCALING_FACTOR_LAYERED_LAYER, refGLPathwayTextureManager);
//		pathwayPoolLayer = new JukeboxHierarchyLayer(generalManager,
//				MAX_LOADED_PATHWAYS, SCALING_FACTOR_POOL_LAYER, refGLPathwayTextureManager);
		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer(generalManager, 1, refGLPathwayTextureManager);
		pathwayLayeredLayer = new JukeboxHierarchyLayer(generalManager, 4, refGLPathwayTextureManager);
		pathwayPoolLayer = new JukeboxHierarchyLayer(generalManager, MAX_LOADED_PATHWAYS, refGLPathwayTextureManager);
		
		pathwayUnderInteractionLayer.setParentLayer(pathwayLayeredLayer);
		pathwayLayeredLayer.setChildLayer(pathwayUnderInteractionLayer);
		pathwayLayeredLayer.setParentLayer(pathwayPoolLayer);
		pathwayPoolLayer.setChildLayer(pathwayLayeredLayer);

		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.7f, -1.4f, 0f));
//		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.7f, -4, 0f));
		
		transformPathwayUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));
		pathwayUnderInteractionLayer.setTransformByPositionIndex(0,
				transformPathwayUnderInteraction);

		infoAreaRenderer = new GLInfoAreaRenderer(generalManager,
				refGLPathwayManager);
		infoAreaRenderer.enableColorMappingArea(true);
		
		memoPad = new GLPathwayMemoPad(generalManager,
				refGLPathwayManager,
				refGLPathwayTextureManager);

		dragAndDrop = new GLDragAndDropPathway(refGLPathwayTextureManager);
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		
		// initialize internal gene selection manager
		ArrayList<EViewInternalSelectionType> alSelectionType = new ArrayList<EViewInternalSelectionType>();
		for(EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			alSelectionType.add(selectionType);
		}		
		pathwayVertexSelectionManager = new GenericSelectionManager(
				alSelectionType, EViewInternalSelectionType.NORMAL);
		
//		vecScaling = new Vec3f(1,1,1);
//		vecTranslation = new Vec3f(0,0,0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		//iGLDisplayListIndexLocal = gl.glGenLists(1);	
		init(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
	
		//iGLDisplayListIndexRemote = gl.glGenLists(1);	
		init(gl);
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
	    time = new SystemTime();
	    ((SystemTime) time).rebase();

		memoPad.init(gl);
		initPathwayData(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(iUniqueId, gl, true);
		
		display(gl);
		
		if (pickingTriggerMouseAdapter.wasMouseReleased())
			dragAndDrop.stopDragAction();
		
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
	
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		
		if (iLazyPathwayLoadingId != -1)
		{
			loadPathwayToUnderInteractionPosition(iLazyPathwayLoadingId);
			iLazyPathwayLoadingId = -1;
		}
		
		checkForHits(gl);
		
		if (dragAndDrop.isDragActionRunning()) {
			dragAndDrop.renderDragThumbnailTexture(gl, this);
		}
		
		if (arSlerpActions.isEmpty() && bSelectionChanged 
				&& selectedVertex != null)
		{
			bSelectionChanged = false;
			bRebuildVisiblePathwayDisplayLists = true;	
		}
		
		if (bRebuildVisiblePathwayDisplayLists)
			rebuildVisiblePathwayDisplayLists(gl);
		
		if (bUpdateReceived)
		{
			// unpack selection item reps
			ArrayList<Integer> iAlSelectionId = alSetSelection.get(1).getSelectionIdArray();
			ArrayList<IGraphItem> alSelectedVertexReps = new ArrayList<IGraphItem>();
			IPathwayItemManager pathwayItemManager = generalManager.getPathwayItemManager();
			for (int iItemIndex = 0; iItemIndex < iAlSelectionId.size(); iItemIndex++)
			{
				alSelectedVertexReps.add(
						(IGraphItem) pathwayItemManager.getItem(iAlSelectionId.get(iItemIndex)));
			}
			loadDependentPathways(gl, alSelectedVertexReps);
			
			bUpdateReceived = false;
		}
		
		time.update();

		doSlerpActions(gl);
		
		renderConnectingLines(gl);
		renderScene(gl);
		renderInfoArea(gl);
	}
	
	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, pathwayVertexSelectionManager);
		buildPathwayPool(gl);
		buildLayeredPathways(gl);
	}


	public void renderScene(final GL gl) {
		
		renderPathwayPool(gl);
		renderPathwayLayered(gl);
		renderPathwayUnderInteraction(gl);

		memoPad.renderMemoPad(gl, this);
	}

	private void buildLayeredPathways(final GL gl) {

		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = 0.9f;
		int iMaxLayers = 4;

		// Create free pathway layer spots
		Transform transform;
		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2.7f, fLayerYPos, 0f));
			
			// DKT horizontal stack
//			transform.setTranslation(new Vec3f(-2.7f + fLayerYPos, 1.1f, 0));
			
			transform.setScale(new Vec3f(SCALING_FACTOR_LAYERED_LAYER,
					SCALING_FACTOR_LAYERED_LAYER,
					SCALING_FACTOR_LAYERED_LAYER));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
//			transform.setRotation(new Rotf(new Vec3f(-0.7f, -1f, 0), fTiltAngleRad));
			
			pathwayLayeredLayer.setTransformByPositionIndex(iLayerIndex,
					transform);

			fLayerYPos -= 1;
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
		
//		// Load KEGG pathways
//		generalManager.getSingelton().getPathwayManager()
//			.loadAllPathwaysByType(EPathwayDatabaseType.KEGG);
//
//		// Load BioCarta pathways
//		generalManager.getSingelton().getPathwayManager()
//			.loadAllPathwaysByType(EPathwayDatabaseType.BIOCARTA);
//		
//		Iterator<IGraph> iterPathwayGraphs = generalManager.getSingelton()
//			.getPathwayManager().getRootPathway().getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN).iterator();
//
//		while(iterPathwayGraphs.hasNext())
//		{
//			pathwayPoolLayer.addElement((iterPathwayGraphs.next()).getId());
//		}
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
		
		if (iPathwayId == -1)
			return;
		
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
				if (layer.equals(pathwayLayeredLayer))
				{
					refGLPathwayTextureManager.renderPathway(gl, this, iPathwayId,
							fTextureTransparency, true);					
				}
				else
				{
					refGLPathwayTextureManager.renderPathway(gl, this, iPathwayId,
							1.0f, true);
				}
			}
			else
			{
				refGLPathwayTextureManager.renderPathway(gl, this, iPathwayId,
						fTextureTransparency, false);
			}
		}

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getHeight();
		
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
	}

	private void renderPathwayPool(final GL gl) {
		
		// Initialize magnification factors with 0 (minimized)
		ArrayList<Integer> alMagnificationFactor = new ArrayList<Integer>();

		for (int iPathwayIndex = 0; iPathwayIndex < MAX_LOADED_PATHWAYS; iPathwayIndex++)
		{
			alMagnificationFactor.add(0);
		}		
		
//		// Clear pathway pool list
//		pathwayPoolLayer.removeAllElements();
//		
//		Iterator<Integer> iterSelectedPathways = 
//			refHashPathwayContainingSelectedVertex2VertexCount.keySet().iterator();
//		
//		while(iterSelectedPathways.hasNext())
//		{
//			pathwayPoolLayer.addElement(iterSelectedPathways.next());
//		}
		
		// Load pathway storage
		int iPathwayId = 0;
		for (int iPathwayIndex = 0; iPathwayIndex < pathwayPoolLayer.getElementList().size(); iPathwayIndex++)
		{
			iPathwayId = pathwayPoolLayer.getElementIdByPositionIndex(iPathwayIndex);
		
			if (iMouseOverPickedPathwayId == iPathwayId)
			{
//				if ((iPathwayIndex - 2 >= 0)
//						&& (alMagnificationFactor.get(iPathwayIndex - 2) < 1))
//				{
//					alMagnificationFactor.set(iPathwayIndex - 2, 1);
//				}
//
//				if ((iPathwayIndex - 1 >= 0)
//						&& (alMagnificationFactor.get(iPathwayIndex - 1) < 2))
//				{
//					alMagnificationFactor.set(iPathwayIndex - 1, 2);
//				}

				alMagnificationFactor.set(iPathwayIndex, 3);

//				if ((iPathwayIndex + 1 < alMagnificationFactor.size())
//						&& (alMagnificationFactor.get(iPathwayIndex + 1) < 2))
//				{
//					alMagnificationFactor.set(iPathwayIndex + 1, 2);
//				}
//
//				if ((iPathwayIndex + 2 < alMagnificationFactor.size())
//						&& (alMagnificationFactor.get(iPathwayIndex + 2) < 1))
//				{
//					alMagnificationFactor.set(iPathwayIndex + 2, 1);
//				}
			} else if (pathwayLayeredLayer.containsElement(iPathwayId)
					|| pathwayUnderInteractionLayer.containsElement(iPathwayId))
			{
				alMagnificationFactor.set(iPathwayIndex, 2);
			} 
			else if (refHashPathwayContainingSelectedVertex2VertexCount
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

//			// Ignore all list elements that are of no interest at the moment 
//			if (alMagnificationFactor.get(iPathwayIndex) == 0)
//				break;

			if (iPathwayId == -1)
				continue;
			
			gl.glPushName(generalManager.getViewGLCanvasManager().getPickingManager()
					.getPickingID(iUniqueId, EPickingType.PATHWAY_POOL_SELECTION, iPathwayId));

			Transform transform = pathwayPoolLayer
					.getTransformByElementId(iPathwayId);
			Vec3f translation = transform.getTranslation();
			gl.glTranslatef(translation.x(), translation.y(), translation.z()
					+ fZPos);

			if (alMagnificationFactor.get(iPathwayIndex) != 0)
			{
				sRenderText = ((PathwayGraph) generalManager
						.getPathwayManager().getItem(iPathwayId)).getTitle();
			}
			
			// Limit pathway name in length
			if(sRenderText.length()> 35 && alMagnificationFactor.get(iPathwayIndex) <= 2)
				sRenderText = sRenderText.subSequence(0, 35) + "...";

			// Append identical vertex count to pathway title
			if (refHashPathwayContainingSelectedVertex2VertexCount.containsKey(iPathwayId))
			{	
				sRenderText = sRenderText
						+ " - "
						+ refHashPathwayContainingSelectedVertex2VertexCount
								.get(iPathwayId).toString();
			}
			
			if (pathwayUnderInteractionLayer.containsElement(iPathwayId))
			{
				textRenderer.setColor(1,0,0,1);
//				gl.glColor4f(1, 0, 0, 1);				
			}
			else
			{
				textRenderer.setColor(0,0,0,1);
//				gl.glColor4f(0, 0, 0, 1);
			}
			
			if (alMagnificationFactor.get(iPathwayIndex) == 3)
			{
				textRenderer.begin3DRendering();	
				textRenderer.draw3D(sRenderText,
						0, 
						0, 
						0,
						0.0057f);  // scale factor
				textRenderer.end3DRendering();
				
				fYPos = 0.12f;

//				GLTextUtils.renderText(gl, sRenderText, 18, 0, 0.06f, 0);
//				fYPos = 0.15f;
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 2)
			{	
				textRenderer.begin3DRendering();	
				textRenderer.draw3D(sRenderText,
						0, 
						0, 
						0,
						0.0047f);  // scale factor
				textRenderer.end3DRendering();
				
				fYPos = 0.12f;
				
//				GLTextUtils.renderText(gl, sRenderText, 12, 0, 0.04f, 0);
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 1)
			{
				textRenderer.begin3DRendering();	
				textRenderer.draw3D(sRenderText,
						0, 
						0, 
						0,
						0.0037f);  // scale factor
				textRenderer.end3DRendering();
				
				fYPos = 0.12f;
				
//				GLTextUtils.renderText(gl, sRenderText, 10, 0, 0.02f, 0);
//				fYPos = 0.07f;
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 0)
			{	
				gl.glColor3f(0, 0, 0);
	
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(0, 0, 0);
				gl.glVertex3f(0, fYPos, 0);
				gl.glVertex3f(0.1f, fYPos, 0);
				gl.glVertex3f(0.1f, 0, 0);
				gl.glEnd();

				fYPos = 0.02f;
			}
			
			gl.glColor4f(0, 0, 0, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(0, fYPos, 0.1f);
			gl.glVertex3f(1f, fYPos, 0.1f);
			gl.glVertex3f(1f, 0, 0.1f);
			gl.glEnd();

			gl.glPopMatrix();
			gl.glPopName();
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
//				fPathwayPoolHeight += 0.15;
				fPathwayPoolHeight += 0.12;
			} 
			else if (alMagnificationFactor.get(iLineIndex) == 2)
			{
				fPathwayPoolHeight += 0.12;
			} 
			else if (alMagnificationFactor.get(iLineIndex) == 1)
			{
//				fPathwayPoolHeight += 0.07;
				fPathwayPoolHeight += 0.12;
			} 
//			else if (alMagnificationFactor.get(iLineIndex) == 0)
//			{
//				fPathwayPoolHeight += 0.01;
//			}

			transform.setTranslation(new Vec3f(-4.0f, -fPathwayPoolHeight, -6));
		}
	}

	private void renderInfoArea(final GL gl) {

		if (selectedVertex != null
//				&& !bMouseOverMemoPad
				&& infoAreaRenderer.isPositionValid())
		{
			infoAreaRenderer.renderInfoArea(gl, selectedVertex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		generalManager.logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);

		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		ArrayList<Integer> iAlOptional = refSetSelection.getOptionalDataArray();
		if (iAlOptional != null && iAlOptional.size() != 0)
		{
			iLazyPathwayLoadingId = iAlOptional.get(0);
		}
		
		ArrayList<Integer> iAlSelectionId = refSetSelection.getSelectionIdArray();
		if (iAlSelectionId != null &&iAlSelectionId.size() != 0)
		{
			selectedVertex = (PathwayVertexGraphItemRep) generalManager
				.getPathwayItemManager().getItem(iAlSelectionId.get(0));
			
			bRebuildVisiblePathwayDisplayLists = true;
//			bSelectionChanged = true;
			infoAreaRenderer.resetPoint();
			
//			refGLPathwayManager.updateSelectionSet(
//					(SetSelection) refSetSelection);
			
			bUpdateReceived = true;
		}
		
		refSetSelection.returnReadToken();	
	}

	public void updateReceiver(Object eventTrigger) {

	}

	private void doSlerpActions(final GL gl) {

		if (arSlerpActions.isEmpty())
			return;
		
		infoAreaRenderer.resetPoint();
				
		SlerpAction tmpSlerpAction = arSlerpActions.get(0);
		
		if (iSlerpFactor == 0)
		{
			tmpSlerpAction.start();
		}
		
		if (iSlerpFactor < 1000)
		{
			// Makes animation rendering speed independent
			iSlerpFactor += 1400 * time.deltaT();
			
			if (iSlerpFactor > 1000)
				iSlerpFactor = 1000;
		}
		
		slerpPathway(gl, tmpSlerpAction);
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
		
		refGLPathwayTextureManager.renderPathway(gl, this, iPathwayId,
				fTextureTransparency, true);

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getHeight();
		
		gl.glTranslatef(0, tmp, 0);
		refGLPathwayManager.renderPathway(gl, iPathwayId, false);
		gl.glTranslatef(0, -tmp, 0);

		gl.glPopMatrix();

		if (iSlerpFactor >= 1000)
		{
			arSlerpActions.remove(slerpAction);
			
			slerpAction.getDestinationHierarchyLayer()
					.setElementVisibilityById(true, iPathwayId);

			iSlerpFactor = 0;
		}

		if ((iSlerpFactor == 0))
			slerpMod.playSlerpSound();
	}


	protected void checkForHits(final GL gl) {
		
		if (pickingTriggerMouseAdapter.getPickedPoint() != null)
			dragAndDrop.setCurrentMousePos(gl, pickingTriggerMouseAdapter.getPickedPoint());
		
		ArrayList<Pick> alHits = null;		
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iPickedElementID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);

					PathwayVertexGraphItemRep pickedVertexRep = (PathwayVertexGraphItemRep) generalManager
						.getPathwayItemManager().getItem(iPickedElementID);
				
					if (pickedVertexRep == null)
						return;
					
					if (iPickedElementID == 0)
					{
						// Remove pathway pool fisheye
						iMouseOverPickedPathwayId = -1;
			
						infoAreaRenderer.resetPoint();
						pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
						return;
					}
					
					// Do not handle picking if a slerp action is in progress
					if (!arSlerpActions.isEmpty())
						return;
					
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:	
					
							if (selectedVertex != null
									&& !selectedVertex.equals(pickedVertexRep))
							{
								loadNodeInformationInBrowser(((PathwayVertexGraphItem)pickedVertexRep.getAllItemsByProp(
										EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
								
								infoAreaRenderer.resetAnimation();
							}
							
							// Remove pathway pool fisheye
							iMouseOverPickedPathwayId = -1;			
					
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
									pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
									return;
								}
					
								loadPathwayToUnderInteractionPosition(iPathwayId);
								pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
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

							
							break;
						case MOUSE_OVER:	
							
							// Reset pick point
							infoAreaRenderer.convertWindowCoordinatesToWorldCoordinates(gl,
									pickingTriggerMouseAdapter.getPickedPoint().x, 
									pickingTriggerMouseAdapter.getPickedPoint().y);
							
							selectedVertex = pickedVertexRep;
							bSelectionChanged = true;
							
							loadNodeInformationInBrowser(((PathwayVertexGraphItem)pickedVertexRep.getAllItemsByProp(
									EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
							
							break;
					}			
				}
				
				pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
			}
		}
			
		alHits = pickingManager.getHits(iUniqueId, EPickingType.PATHWAY_TEXTURE_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickedPathwayTextureID = pickingManager.getExternalIDFromPickingID(
							iUniqueId, tempPick.getPickingID());

					iMouseOverPickedPathwayId = -1;
					infoAreaRenderer.resetPoint();
					
					switch (tempPick.getPickingMode())
					{						
						case DRAGGED:	
							
							if (!dragAndDrop.isDragActionRunning())
								dragAndDrop.startDragAction(iPickedPathwayTextureID);
							
							break;
							
						case CLICKED:

							loadPathwayToUnderInteractionPosition(iPickedPathwayTextureID);
							break;
					}
				}
				
				pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_TEXTURE_SELECTION);
			}
		}
			
		alHits = pickingManager.getHits(iUniqueId, EPickingType.PATHWAY_POOL_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickedPathwayID = pickingManager.getExternalIDFromPickingID(
							iUniqueId, tempPick.getPickingID());
					
					infoAreaRenderer.resetPoint();
					
					switch (tempPick.getPickingMode())
					{			
						case CLICKED:	

							loadPathwayToUnderInteractionPosition(iPickedPathwayID);
							break;
							
						case MOUSE_OVER:
							
							iMouseOverPickedPathwayId = iPickedPathwayID;
							playPathwayPoolTickSound();
							
							break;
					}
				}
				
				pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_POOL_SELECTION);
			}
		}
			
		alHits = pickingManager.getHits(iUniqueId, EPickingType.MEMO_PAD_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickedElementID = pickingManager.getExternalIDFromPickingID(
							iUniqueId, tempPick.getPickingID());
					
					infoAreaRenderer.resetPoint();
					
					switch (tempPick.getPickingMode())
					{										
						case CLICKED:
							
							if (iPickedElementID == GLPathwayMemoPad.MEMO_PAD_PICKING_ID)
							{
								if (dragAndDrop.getDraggedObjectedId() != -1)
								{
									memoPad.addPathwayToMemoPad(dragAndDrop.getDraggedObjectedId());
									dragAndDrop.stopDragAction();
								}
							} 
							break;
						
						case DRAGGED:
							
							if (iPickedElementID == GLPathwayMemoPad.MEMO_PAD_TRASH_CAN_PICKING_ID)
							{
								if (dragAndDrop.getDraggedObjectedId() != -1)
								{
									// Remove dragged object from memo pad
									memoPad.removePathwayFromMemoPad(dragAndDrop.getDraggedObjectedId());
									dragAndDrop.stopDragAction();
								}
							}
							break;
					}
				}
				
				pickingManager.flushHits(iUniqueId, EPickingType.MEMO_PAD_SELECTION);
			}
		}
	}

	private void loadPathwayToUnderInteractionPosition(final int iPathwayId) {

		generalManager
				.logMsg(this.getClass().getSimpleName()
								+ ": loadPathwayToUnderInteractionPosition(): Pathway with ID "
								+ iPathwayId + " is under interaction.",
						LoggerType.VERBOSE);

		// Check if pathway is already under interaction
		if (pathwayUnderInteractionLayer.containsElement(iPathwayId))
			return;
		
		loadNodeInformationInBrowser(((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getExternalLink());
		
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < 1000)
			return;

		arSlerpActions.clear();

		// Check if selected pathway is loaded.
		if (!generalManager.getPathwayManager().hasItem(iPathwayId))
		{
			return;
		}

		// Slerp current pathway back to layered view
		if (pathwayUnderInteractionLayer.getElementIdByPositionIndex(0) != -1)
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
			pathwayPoolLayer.addElement(iPathwayId);

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
		ArrayList<Integer> iAlOptional = new ArrayList<Integer>();
		iAlOptional.add(iPathwayId);
		ArrayList<Integer> iAlGroup = new ArrayList<Integer>();
		iAlGroup.add(0);
		ArrayList<Integer> iAlSelection = new ArrayList<Integer>();
		iAlSelection.add(0);
		
		alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlSelection,
				iAlGroup, iAlOptional);
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
		pathwayPoolLayer.removeAllElements();
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
					.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0]).getId();

			pathwayPoolLayer.addElement(iPathwayId);
			
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
		
//		refGLPathwayManager.clearOldPickingIDs();

		if (selectedVertex != null)
		{
			// Write currently selected vertex to selection set
			ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(1);
			ArrayList<Integer> iAlTmpDepth = new ArrayList<Integer> ();
			iAlTmpSelectionId.add(selectedVertex.getId());
			iAlTmpDepth.add(0);
			ArrayList<Integer> iAlOptional = new ArrayList<Integer>();
			alSetSelection.get(1).getWriteToken();
			alSetSelection.get(1).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpDepth, iAlOptional);
			alSetSelection.get(1).returnWriteToken();
		}
			
		refGLPathwayManager.performIdenticalNodeHighlighting();
		
		// Update display list if something changed
		// Rebuild display lists for visible pathways in layered view
		Iterator<Integer> iterVisiblePathway = pathwayLayeredLayer
				.getElementList().iterator();

		while (iterVisiblePathway.hasNext())
		{
			refGLPathwayManager.buildPathwayDisplayList(gl, this, iterVisiblePathway.next());
		}

		// Rebuild display lists for visible pathways in focus position
		if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
				&& !pathwayLayeredLayer.containsElement(pathwayUnderInteractionLayer
				.getElementIdByPositionIndex(0)))
		{
			refGLPathwayManager.buildPathwayDisplayList(gl, this,
					pathwayUnderInteractionLayer.getElementIdByPositionIndex(0));
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

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) generalManager
				.getCommandManager().createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}

	public void setMappingRowCount(final int iMappingRowCount) {
		
		refGLPathwayManager.setMappingRowCount(iMappingRowCount);
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
		Vec3f tmpLineColor = new PathwayRenderStyle().getLayerConnectionLinesColor();
		gl.glColor4f(tmpLineColor.x(), tmpLineColor.y(), tmpLineColor.z(), 1);
		
		
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
									 (((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayIdSrc)).getHeight()
											 -tmpVertexGraphItemRepSrc.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
							
							vecMatDest.set(tmpVertexGraphItemRepDest.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X,
									 (((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayIdDest)).getHeight()
											 -tmpVertexGraphItemRepDest.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
							
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
	
//	private void calculatePathwayScaling(final GL gl, final int iPathwayId) {
//		
////		if (refHashGLcontext2TextureManager.get(gl) == null)
////			return;
//		
//		int iImageWidth = ((PathwayGraph)generalManager.getSingelton()
//				.getPathwayManager().getItem(iPathwayId)).getWidth();
//		int iImageHeight = ((PathwayGraph)generalManager.getSingelton()
//				.getPathwayManager().getItem(iPathwayId)).getHeight();
//	
//		float fAspectRatio = (float)iImageWidth / (float)iImageHeight;
//		float fPathwayScalingFactor = 0;
//		
//		if (((PathwayGraph)generalManager.getSingelton().getPathwayManager()
//				.getItem(iPathwayId)).getType().equals(EPathwayDatabaseType.BIOCARTA))
//		{
//			fPathwayScalingFactor = 5;
//		}
//		else
//		{
//			fPathwayScalingFactor = 3.2f;
//		}
//		
//		float fTmpPathwayWidth = iImageWidth * GLPathwayManager.SCALING_FACTOR_X * fPathwayScalingFactor;
//		float fTmpPathwayHeight = iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * fPathwayScalingFactor;
//		
//		if (fTmpPathwayWidth > (viewFrustum.getRight() - viewFrustum.getLeft())
//				&& fTmpPathwayWidth > fTmpPathwayHeight)
//		{			
//			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft()) / (iImageWidth * GLPathwayManager.SCALING_FACTOR_X) * 0.9f);
//			vecScaling.setY(vecScaling.x());	
//
//			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - 
//					iImageWidth * GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, 
//					(viewFrustum.getTop() - viewFrustum.getBottom() - 
//							iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
//		}
//		else if (fTmpPathwayHeight > (viewFrustum.getTop() - viewFrustum.getBottom()))
//		{
//			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom()) / (iImageHeight * GLPathwayManager.SCALING_FACTOR_Y) * 0.9f);
//			vecScaling.setX(vecScaling.y());
//
//			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - 
//							iImageWidth * GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, 
//							(viewFrustum.getTop() - viewFrustum.getBottom() - 
//									iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
//		}
//		else
//		{
//			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);			
//		
//			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f - fTmpPathwayWidth / 2.0f,
//					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f - fTmpPathwayHeight / 2.0f, 0);
//		}
//	}
	
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}
}