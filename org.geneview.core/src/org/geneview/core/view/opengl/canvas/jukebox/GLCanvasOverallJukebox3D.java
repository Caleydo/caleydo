package org.geneview.core.view.opengl.canvas.jukebox;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.PickingManager;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.util.slerp.SlerpMod;
import org.geneview.core.util.system.SystemTime;
import org.geneview.core.util.system.Time;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

import com.sun.opengl.util.BufferUtil;

/**
 * Implementation of the overall Jukebox setup.
 * It supports the user with the ability to navigate
 * and interact with arbitrary views.
 * 
 * @author Marc Streit
 *
 */
public class GLCanvasOverallJukebox3D
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {
	
	private static final int MAX_LOADED_VIEWS = 100;
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 2f;
	private static final float SCALING_FACTOR_STACK_LAYER = 1f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.1f;
	
	private static final int VIEW_PICKING = 0;	

	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private ArrayList<SlerpAction> arSlerpActions;
	private Time time;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;
	
	private PickingManager pickingManager;
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	private boolean bRebuildVisiblePathwayDisplayLists = false;
	
	private GLConnectionLineRenderer glConnectionLineRenderer;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasOverallJukebox3D(final IGeneralManager generalManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(generalManager, null, iViewId, iParentContainerId, "");

		this.refViewCamera.setCaller(this);
		
		underInteractionLayer = new JukeboxHierarchyLayer(refGeneralManager,
				1, 
				SCALING_FACTOR_UNDER_INTERACTION_LAYER, 
				null);
		
		stackLayer = new JukeboxHierarchyLayer(refGeneralManager,
				4, 
				SCALING_FACTOR_STACK_LAYER, 
				null);
		
		poolLayer = new JukeboxHierarchyLayer(refGeneralManager,
				MAX_LOADED_VIEWS, 
				SCALING_FACTOR_POOL_LAYER, 
				null);
		
		underInteractionLayer.setParentLayer(stackLayer);
		stackLayer.setChildLayer(underInteractionLayer);
		stackLayer.setParentLayer(poolLayer);
		poolLayer.setChildLayer(stackLayer);
		
		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.5f, -1f, 0f));
		transformPathwayUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));
		underInteractionLayer.setTransformByPositionIndex(0,
				transformPathwayUnderInteraction);
		
		pickingManager = refGeneralManager.getSingelton()
			.getViewGLCanvasManager().getPickingManager();
		
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) openGLCanvasDirector
			.getJoglCanvasForwarder().getJoglMouseListener();
		
		arSlerpActions = new ArrayList<SlerpAction>();
		
		glConnectionLineRenderer = new GLConnectionLineRenderer(refGeneralManager,
				underInteractionLayer, stackLayer, poolLayer);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		
	    time = new SystemTime();
	    ((SystemTime) time).rebase();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
		buildStackLayer(gl);
		
		retrieveContainedViews(gl);
		
		setInitGLDone();
	}

	private void retrieveContainedViews(final GL gl) {
		
		Iterator<IGLCanvasUser> iterCanvasUser = 
			refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getAllGLCanvasUsers().iterator();
		
		while(iterCanvasUser.hasNext())
		{
			IGLCanvasUser tmpView = iterCanvasUser.next();
			
			if(tmpView == this)
				continue;
			
			int iViewId = tmpView.getId();
			
			if (underInteractionLayer.getElementList().size() < 1)
			{
				underInteractionLayer.addElement(iViewId);
				underInteractionLayer.setElementVisibilityById(true, iViewId);
			}
			
			stackLayer.addElement(iViewId);
			stackLayer.setElementVisibilityById(true, iViewId);
			
			tmpView.initGLCanvas(gl);
			
			pickingManager.getPickingID(this, VIEW_PICKING, tmpView.getId());
		}
	}
	
	private void buildStackLayer(final GL gl) {

//		float fTiltAngleDegree = 57; // degree
//		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
//		float fLayerYPos = 1.1f;
//		int iMaxLayers = 4;

//		// Create free pathway layer spots
//		Transform transform;
//		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
//		{
//			// Store current model-view matrix
//			transform = new Transform();
//			transform.setTranslation(new Vec3f(-2.7f, fLayerYPos, 0f));
//			transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
//					SCALING_FACTOR_STACK_LAYER,
//					SCALING_FACTOR_STACK_LAYER));
//			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
//			stackLayer.setTransformByPositionIndex(iLayerIndex,
//					transform);
//
//			fLayerYPos -= 1f;
//		}
		
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		
		Transform transform = new Transform();

		transform.setTranslation(new Vec3f(0, -2.5f, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(-1f, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(0, transform);

		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 1.5f, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(1f, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(1, transform);

		transform = new Transform();
		transform.setTranslation(new Vec3f(-2, 0, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(2, transform);

		transform = new Transform();
		transform.setTranslation(new Vec3f(2, 0, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(0, 1f, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(3, transform);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) {

		handlePicking(gl);
		
//		if (bRebuildVisiblePathwayDisplayLists)
//			rebuildVisiblePathwayDisplayLists(gl);
		
		time.update();
		doSlerpActions(gl);
		
//		renderPoolLayer(gl);
		renderStackLayer(gl);
		renderUnderInteractionLayer(gl);
		
		glConnectionLineRenderer.render(gl);
	}
	
	private void renderUnderInteractionLayer(final GL gl) {
		
		// Check if a pathway is currently under interaction
		if (underInteractionLayer.getElementList().size() == 0)
			return;

		int iViewId = underInteractionLayer.getElementIdByPositionIndex(0);
		gl.glPushName(pickingManager.getPickingID(this, VIEW_PICKING, iViewId));
		renderViewById(gl, iViewId, underInteractionLayer);
		gl.glPopName();
	}
	
	private void renderStackLayer(final GL gl) {

		Iterator<Integer> iterElementList = stackLayer.getElementList().iterator();
		int iViewId = 0;
		
		while(iterElementList.hasNext())
		{		
			iViewId = iterElementList.next();		
			
//			// Check if pathway is visible
//			if(!stackLayer.getElementVisibilityById(iPathwayId))
//				continue;
			
			gl.glPushName(pickingManager.getPickingID(this, VIEW_PICKING, iViewId));
			renderViewById(gl, iViewId, stackLayer);		
			gl.glPopName();
		}
	}
	
	private void renderViewById(final GL gl,
			final int iViewId, 
			final JukeboxHierarchyLayer layer) {
		
		// Check if view is visible
		if(!layer.getElementVisibilityById(iViewId))
			return;
		
		gl.glPushMatrix();
		
		Transform transform = layer.getTransformByElementId(iViewId);
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();		
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z() );
		
		((IGLCanvasUser)refGeneralManager.getSingelton()
				.getViewGLCanvasManager().getItem(iViewId)).renderPart(gl);
		
		gl.glPopMatrix();	
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
		
		slerpView(gl, arSlerpActions.get(0));
		// selectedVertex = null;
	}
	
	private void slerpView(final GL gl, SlerpAction slerpAction) {

		int iViewId = slerpAction.getElementId();
		SlerpMod slerpMod = new SlerpMod();
		
		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getOriginPosIndex()), slerpAction
				.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()),
				(int)iSlerpFactor / 1000f);

		gl.glPushMatrix();
		
		slerpMod.applySlerp(gl, transform);
		
		((IGLCanvasUser)refGeneralManager.getSingelton()
				.getViewGLCanvasManager().getItem(iViewId)).renderPart(gl);

		gl.glPopMatrix();

		if (iSlerpFactor >= 1000)
		{
			slerpAction.getDestinationHierarchyLayer()
					.setElementVisibilityById(true, iViewId);

			arSlerpActions.remove(slerpAction);
			iSlerpFactor = 0;
		}

		if ((iSlerpFactor == 0))
			slerpMod.playSlerpSound();
	}
	
	private void handlePicking(GL gl)
	{
		Point pickPoint = null;

		boolean bMouseReleased =
			pickingTriggerMouseAdapter.wasMouseReleased();

		
		if (pickingTriggerMouseAdapter.wasMousePressed()
				|| bMouseReleased)
		{			
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			//bIsMouseOverPickingEvent = false;
		}
		else
		{
			return;
		}
		
		
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

		//gl.glPushName(0);

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				5.0, 5.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		// FIXME: values have to be taken from XML file!!
		//gl.glOrtho(-4.0f, 4.0f, -4*h, 4*h, 1.0f, 1000.0f);
		gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		renderStackLayer(gl);
		renderUnderInteractionLayer(gl);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		System.out.println("Picking Buffer: " + iArPickingBuffer[0]);
		System.out.println("MAX int:" +Integer.MAX_VALUE);
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint);
	}
	
	
	protected void processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], final Point pickPoint) 
	{

		pickingManager.processHits(this, iHitCount, iArPickingBuffer, EPickingMode.ReplacePick, true);
		
		if(pickingManager.getHits(this, VIEW_PICKING) != null)
		{
			ArrayList<Integer> tempList = pickingManager.getHits(this, VIEW_PICKING);
			
			if (tempList != null)
			{
				if (tempList.size() != 0 )
				{
					int iViewId = pickingManager.getExternalIDFromPickingID(this, tempList.get(0));
					System.out.println("Picked object:" +iViewId);
					
					loadViewToUnderInteractionLayer(iViewId);
				}
			}
		}	
		
		pickingManager.flushHits(this, VIEW_PICKING);
	}
	
	private void loadViewToUnderInteractionLayer(final int iViewId) {

		refGeneralManager
				.getSingelton()
				.logMsg(this.getClass().getSimpleName()
								+ ": loadPathwayToUnderInteractionPosition(): View with ID "
								+ iViewId + " is under interaction.",
						LoggerType.VERBOSE);

		// Check if pathway is already under interaction
		if (underInteractionLayer.containsElement(iViewId))
			return;
				
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < 1000)
			return;

		arSlerpActions.clear();

		// Slerp current pathway back to layered view
		if (!underInteractionLayer.getElementList().isEmpty())
		{
			SlerpAction reverseSlerpAction = new SlerpAction(
					underInteractionLayer.getElementIdByPositionIndex(0),
					underInteractionLayer, true);

			arSlerpActions.add(reverseSlerpAction);
		}

		SlerpAction slerpAction;

		// Prevent slerp action if pathway is already in layered view
		if (!stackLayer.containsElement(iViewId))
		{
			// Slerp to layered pathway view
			slerpAction = new SlerpAction(iViewId, poolLayer, false);

			arSlerpActions.add(slerpAction);
		}

		// Slerp from layered to under interaction position
		slerpAction = new SlerpAction(iViewId, stackLayer, false);

		arSlerpActions.add(slerpAction);
		iSlerpFactor = 0;

		bRebuildVisiblePathwayDisplayLists = true;
		//selectedVertex = null;
	}
	
//	private void rebuildVisiblePathwayDisplayLists(final GL gl) {
//
//		// Reset rebuild trigger flag
//		bRebuildVisiblePathwayDisplayLists = false;
//		
////		if (selectedVertex != null)
////		{
////			// Write currently selected vertex to selection set
////			int[] iArTmpSelectionId = new int[1];
////			int[] iArTmpDepth = new int[1];
////			iArTmpSelectionId[0] = selectedVertex.getId();
////			iArTmpDepth[0] = 0;
////			alSetSelection.get(0).getWriteToken();
////			alSetSelection.get(0).updateSelectionSet(iUniqueId, iArTmpSelectionId, iArTmpDepth, new int[0]);
////			alSetSelection.get(0).returnWriteToken();
////		}
//			
//		// Update display list if something changed
//		// Rebuild display lists for visible pathways in layered view
//		Iterator<Integer> iterVisiblePathway = stackLayer.getElementList().iterator();
//
//		while (iterVisiblePathway.hasNext())
//		{
//			refGLPathwayManager.buildPathwayDisplayList(gl, iterVisiblePathway.next());
//		}
//
//		// Rebuild display lists for visible pathways in focus position
//		if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
//				&& !pathwayLayeredLayer.containsElement(pathwayUnderInteractionLayer
//				.getElementIdByPositionIndex(0)))
//		{
//			refGLPathwayManager.buildPathwayDisplayList(gl, pathwayUnderInteractionLayer
//							.getElementIdByPositionIndex(0));
//		}
//		
//		// Cleanup unused textures
//		refGLPathwayTextureManager.unloadUnusedTextures(getVisiblePathways());
//
//		// Trigger update on current selection
//		//alSetSelection.get(0).updateSelectionSet(iUniqueId);
//	}
}
