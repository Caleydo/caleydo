package org.geneview.core.view.opengl.canvas.jukebox;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLEventListener;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.util.slerp.SlerpMod;
import org.geneview.core.util.system.SystemTime;
import org.geneview.core.util.system.Time;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

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
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 0.5f;
	private static final float SCALING_FACTOR_STACK_LAYER = 0.5f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.1f;
	
	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private ArrayList<SlerpAction> arSlerpActions;
	private Time time;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;
	
	private GLConnectionLineRenderer glConnectionLineRenderer;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasOverallJukebox3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
		underInteractionLayer = new JukeboxHierarchyLayer(generalManager,
				1, 
				SCALING_FACTOR_UNDER_INTERACTION_LAYER, 
				null);
		
		stackLayer = new JukeboxHierarchyLayer(generalManager,
				4, 
				SCALING_FACTOR_STACK_LAYER, 
				null);
		
		poolLayer = new JukeboxHierarchyLayer(generalManager,
				MAX_LOADED_VIEWS, 
				SCALING_FACTOR_POOL_LAYER, 
				null);
		
		underInteractionLayer.setParentLayer(stackLayer);
		stackLayer.setChildLayer(underInteractionLayer);
		stackLayer.setParentLayer(poolLayer);
		poolLayer.setChildLayer(stackLayer);
		
		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(0, 0, 0f));
		transformPathwayUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));
		underInteractionLayer.setTransformByPositionIndex(0, transformPathwayUnderInteraction);
		
		arSlerpActions = new ArrayList<SlerpAction>();
		
		glConnectionLineRenderer = new GLConnectionLineRenderer(generalManager,
				underInteractionLayer, stackLayer, poolLayer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl) {
	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.geneview.core.view.opengl.util.JukeboxHierarchyLayer, org.geneview.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{		
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
	    time = new SystemTime();
	    ((SystemTime) time).rebase();
	    
		buildStackLayer(gl);
		retrieveContainedViews(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(iUniqueId, gl, true);
//		if(bIsDisplayListDirtyLocal)
//		{
//			buildDisplayList(gl, iGLDisplayListIndexLocal);
//			bIsDisplayListDirtyLocal = false;			
//		}	
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
//		gl.glCallList(iGLDisplayListIndexLocal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
	
//		if(bIsDisplayListDirtyRemote)
//		{
//			buildPathwayDisplayList(gl, iGLDisplayListIndexRemote);
//			bIsDisplayListDirtyRemote = false;
//		}	
		display(gl);
//		gl.glCallList(iGLDisplayListIndexRemote);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		checkForHits();
		
//		if (bRebuildVisiblePathwayDisplayLists)
//			rebuildVisiblePathwayDisplayLists(gl);
		
		time.update();

		doSlerpActions(gl);
		
//		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
//		renderPoolLayer(gl);
		renderStackLayer(gl);
		renderUnderInteractionLayer(gl);
//		gl.glEndList();
		
		glConnectionLineRenderer.render(gl);
		
		// TODO: add dirty flag
//		gl.glCallList(iGLDisplayListIndex);
		
	}

	private void retrieveContainedViews(final GL gl) {
		
		Iterator<GLEventListener> iterGLEventListener = 
			generalManager.getSingelton().getViewGLCanvasManager()
			.getAllGLEventListeners().iterator();
		
		while(iterGLEventListener.hasNext())
		{
			AGLCanvasUser tmpGLEventListener = (AGLCanvasUser)iterGLEventListener.next();
			
			if(tmpGLEventListener == this)
				continue;
			
			int iViewId = ((AGLCanvasUser)tmpGLEventListener).getId();
			
			if (underInteractionLayer.getElementList().size() < 1)
			{
				underInteractionLayer.addElement(iViewId);
				underInteractionLayer.setElementVisibilityById(true, iViewId);
			}
			
			stackLayer.addElement(iViewId);
			stackLayer.setElementVisibilityById(true, iViewId);
			
			tmpGLEventListener.initRemote(gl, iUniqueId, stackLayer, pickingTriggerMouseAdapter);
			
			pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_PICKING, iViewId);
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
		
		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		
		// TOP BUCKET WALL
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 8 * SCALING_FACTOR_STACK_LAYER, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(0, transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(1, transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(2, transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(8 * SCALING_FACTOR_STACK_LAYER, 0, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER));		
		transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(3, transform);
	}
		
	private void renderBucketWall(final GL gl) {
		
		gl.glColor3f(0f, 1f, 0f);
		gl.glLineWidth(1);
		
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();
		
//		GLSharedObjects.drawAxis(gl);
	}
	
	private void renderUnderInteractionLayer(final GL gl) {
		
		// Check if a pathway is currently under interaction
		if (underInteractionLayer.getElementList().size() == 0)
			return;

		int iViewId = underInteractionLayer.getElementIdByPositionIndex(0);
		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_PICKING, iViewId));
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
			
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_PICKING, iViewId));
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

//		GLSharedObjects.drawAxis(gl);

		renderBucketWall(gl);
		
		((AGLCanvasUser) generalManager.getSingelton()
				.getViewGLCanvasManager().getItem(iViewId)).displayRemote(gl);
		
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
		
		((AGLCanvasUser) generalManager.getSingelton()
				.getViewGLCanvasManager().getItem(iViewId)).displayRemote(gl);

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
	
	protected void checkForHits() 
	{
		ArrayList<Pick> alHits = null;		
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.VIEW_PICKING);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					int iViewId = pickingManager.getExternalIDFromPickingID(
							iUniqueId, alHits.get(iCount).getPickingID());					
					
					loadViewToUnderInteractionLayer(iViewId);
				}
			}
		}	
		
		pickingManager.flushHits(iUniqueId, EPickingType.VIEW_PICKING);
	}
	
	private void loadViewToUnderInteractionLayer(final int iViewId) {

//		generalManager
//				.getSingelton()
//				.logMsg(this.getClass().getSimpleName()
//								+ ": loadPathwayToUnderInteractionPosition(): View with ID "
//								+ iViewId + " is under interaction.",
//						LoggerType.VERBOSE);

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

//		bRebuildVisiblePathwayDisplayLists = true;
		//selectedVertex = null;
	}

	@Override
	public void updateReceiver(Object eventTrigger) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		// TODO Auto-generated method stub
		
	}
}
