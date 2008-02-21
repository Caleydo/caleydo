package org.geneview.core.view.opengl.canvas.pathway;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IPathwayItemManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.util.slerp.SlerpMod;
import org.geneview.core.util.sound.SoundPlayer;
import org.geneview.core.util.system.SystemTime;
import org.geneview.core.util.system.Time;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.util.GLDragAndDrop;
import org.geneview.core.view.opengl.util.GLInfoAreaRenderer;
import org.geneview.core.view.opengl.util.GLPathwayMemoPad;
import org.geneview.core.view.opengl.util.GLTextUtils;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

/**
 * Jukebox setup for pathways that supports slerp animation.
 * 
 * @author Marc Streit
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

	private float fTextureTransparency = 0.6f;
	
	private boolean bRebuildVisiblePathwayDisplayLists = false;
	private boolean bIsMouseOverPickingEvent = false;
	private boolean bEnablePathwayTextures = true;
	private boolean bMouseOverMemoPad = false;
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
	 * Constructor.
	 * 
	 */
	public GLCanvasJukeboxPathway3D(final IGeneralManager refGeneralManager,
			int iViewID,
			int iGLCanvasID,
			String sLabel) {

		super(refGeneralManager, iViewID, iGLCanvasID, sLabel);

		refGLPathwayManager = new GLPathwayManager(refGeneralManager);
		refGLPathwayTextureManager = new GLPathwayTextureManager(
				refGeneralManager);
		arSlerpActions = new ArrayList<SlerpAction>();

		refHashPoolLinePickId2PathwayId = new HashMap<Integer, Integer>();
		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();

		// Create Jukebox hierarchy
		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer(refGeneralManager,
				1, SCALING_FACTOR_UNDER_INTERACTION_LAYER, refGLPathwayTextureManager);
		pathwayLayeredLayer = new JukeboxHierarchyLayer(refGeneralManager,
				4, SCALING_FACTOR_LAYERED_LAYER, refGLPathwayTextureManager);
		pathwayPoolLayer = new JukeboxHierarchyLayer(refGeneralManager,
				MAX_LOADED_PATHWAYS, SCALING_FACTOR_POOL_LAYER, refGLPathwayTextureManager);
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

		infoAreaRenderer = new GLInfoAreaRenderer(refGeneralManager,
				refGLPathwayManager);
		infoAreaRenderer.enableColorMappingArea(true);
		
		memoPad = new GLPathwayMemoPad(refGeneralManager,
				refGLPathwayManager,
				refGLPathwayTextureManager);

		dragAndDrop = new GLDragAndDrop(refGLPathwayTextureManager);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		//iGLDisplayListIndexLocal = gl.glGenLists(1);	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL)
	 */
	public void initRemote(final GL gl)
	{
		//iGLDisplayListIndexRemote = gl.glGenLists(1);	
		init(gl);
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
	    time = new SystemTime();
	    ((SystemTime) time).rebase();

		memoPad.init(gl);
		initPathwayData(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(this, gl, pickingTriggerMouseAdapter, true);
		
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
	
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		
		if (iLazyPathwayLoadingId != -1)
		{
			loadPathwayToUnderInteractionPosition(iLazyPathwayLoadingId);
			iLazyPathwayLoadingId = -1;
		}
		
		checkForHits(gl);
		
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
			IPathwayItemManager pathwayItemManager = generalManager.getSingelton().getPathwayItemManager();
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
		
		renderConnectingLines(gl);
		renderScene(gl);
		renderInfoArea(gl);
	}
	
	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, alSetSelection);
		buildPathwayPool(gl);
		buildLayeredPathways(gl);
	}


	public void renderScene(final GL gl) {
		
		renderPathwayPool(gl);
		renderPathwayLayered(gl);
		renderPathwayUnderInteraction(gl);

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
		generalManager.getSingelton().getPathwayManager()
			.loadAllPathwaysByType(EPathwayDatabaseType.KEGG);

		// Load BioCarta pathways
		generalManager.getSingelton().getPathwayManager()
			.loadAllPathwaysByType(EPathwayDatabaseType.BIOCARTA);
		
		Iterator<IGraph> iterPathwayGraphs = generalManager.getSingelton()
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
				if (layer.equals(pathwayLayeredLayer))
				{
					refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
							fTextureTransparency, true);					
				}
				else
				{
					refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
							1.0f, true);
				}
			}
			else
			{
				refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
						fTextureTransparency, false);
			}
		}

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * ((PathwayGraph)generalManager
				.getSingelton().getPathwayManager().getItem(iPathwayId)).getHeight();
		
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
				sRenderText = ((PathwayGraph) generalManager.getSingelton()
						.getPathwayManager().getItem(iPathwayId)).getTitle();
			}
			
			// Limit pathway name in length
			if(sRenderText.length()> 35 && alMagnificationFactor.get(iPathwayIndex) <= 2)
				sRenderText = sRenderText.subSequence(0, 35) + "...";
			
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
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 2)
			{	
				GLTextUtils.renderText(gl, sRenderText, 12, 0, 0.04f, 0);
				fYPos = 0.1f;
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 1)
			{
				GLTextUtils.renderText(gl, sRenderText, 10, 0, 0.02f, 0);
				fYPos = 0.07f;
			} 
			else if (alMagnificationFactor.get(iPathwayIndex) == 0)
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
			} 
			else if (alMagnificationFactor.get(iLineIndex) == 2)
			{
				fPathwayPoolHeight += 0.1;
			} 
			else if (alMagnificationFactor.get(iLineIndex) == 1)
			{
				fPathwayPoolHeight += 0.07;
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

		generalManager.getSingelton().logMsg(
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
			selectedVertex = (PathwayVertexGraphItemRep) generalManager.getSingelton()
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

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * ((PathwayGraph)generalManager
				.getSingelton().getPathwayManager().getItem(iPathwayId)).getHeight();
		
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


	protected void checkForHits(final GL gl)
	{
		ArrayList<Pick> alHits = null;		
		
		alHits = pickingManager.getHits(this, GLPathwayManager.PATHWAY_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
//				boolean bSelectionCleared = false;
//				boolean bMouseOverCleared = false;					
				
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iPickedElementID = pickingManager.getExternalIDFromPickingID(this, iPickingID);

					PathwayVertexGraphItemRep pickedVertexRep = (PathwayVertexGraphItemRep) generalManager
						.getSingelton().getPathwayItemManager().getItem(iPickedElementID);
				
					if (pickedVertexRep == null)
						return;
					
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:	

							if (iPickedElementID == 0)
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
							if (iPickedElementID < MAX_LOADED_PATHWAYS)
							{
								int iPathwayId = refHashPoolLinePickId2PathwayId
										.get(iPickedElementID);
					
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
							else if (iPickedElementID >= PATHWAY_TEXTURE_PICKING_ID_RANGE_START 
									&& iPickedElementID < FREE_PICKING_ID_RANGE_START)
							{
								if (bIsMouseOverPickingEvent)
									return;
								
								if (dragAndDrop.isDragActionRunning())
								{
									iMouseOverPickedPathwayId = 
										refGLPathwayTextureManager.getPathwayIdByPathwayTexturePickingId(iPickedElementID);
								}
								else
								{
									dragAndDrop.startDragAction(
											refGLPathwayTextureManager.getPathwayIdByPathwayTexturePickingId(iPickedElementID));
								}
								
								return;
							}
					
							if (iPickedElementID == GLPathwayMemoPad.MEMO_PAD_PICKING_ID)
							{
								bMouseOverMemoPad = true;
							} 
							else if (iPickedElementID == GLPathwayMemoPad.MEMO_PAD_TRASH_CAN_PICKING_ID)
							{
								// Remove dragged object from memo pad
								memoPad.removePathwayFromMemoPad(
										dragAndDrop.getDraggedObjectedId());
								
								dragAndDrop.stopDragAction();
							}
					
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

							
							break;
						case MOUSE_OVER:
							
							if (selectedVertex == null ||
									(selectedVertex != null && !selectedVertex.equals(pickedVertexRep)))
							{
								// Reset pick point
								infoAreaRenderer.convertWindowCoordinatesToWorldCoordinates(gl,
										pickingTriggerMouseAdapter.getPickedPoint().x, 
										pickingTriggerMouseAdapter.getPickedPoint().y);
								
								selectedVertex = pickedVertexRep;
								bSelectionChanged = true;
							}
							
							break;
					}			
				}
			}
		}
		
		pickingManager.flushHits(this, GLPathwayManager.PATHWAY_SELECTION);
	}

	private void loadPathwayToUnderInteractionPosition(final int iPathwayId) {

		generalManager
				.getSingelton()
				.logMsg(this.getClass().getSimpleName()
								+ ": loadPathwayToUnderInteractionPosition(): Pathway with ID "
								+ iPathwayId + " is under interaction.",
						LoggerType.VERBOSE);

		// Check if pathway is already under interaction
		if (pathwayUnderInteractionLayer.containsElement(iPathwayId))
			return;
		
		loadNodeInformationInBrowser(((PathwayGraph)generalManager
				.getSingelton().getPathwayManager().getItem(iPathwayId)).getExternalLink());
		
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < 1000)
			return;

		arSlerpActions.clear();

		// Check if selected pathway is loaded.
		if (!generalManager.getSingelton().getPathwayManager().hasItem(iPathwayId))
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
		
//		refGLPathwayManager.clearOldPickingIDs();

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
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.LOAD_URL_IN_BROWSER);

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
									 (((PathwayGraph)generalManager.getSingelton().getPathwayManager().getItem(iPathwayIdSrc)).getHeight()
											 -tmpVertexGraphItemRepSrc.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y, 0);
							
							vecMatDest.set(tmpVertexGraphItemRepDest.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X,
									 (((PathwayGraph)generalManager.getSingelton().getPathwayManager().getItem(iPathwayIdDest)).getHeight()
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
}