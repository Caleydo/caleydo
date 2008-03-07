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
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.util.slerp.SlerpMod;
import org.geneview.core.util.system.SystemTime;
import org.geneview.core.util.system.Time;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
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
	
	private static final int MAX_LOADED_VIEWS = 10;
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 0.5f;
	private static final float SCALING_FACTOR_TRANSITION_LAYER = 0.05f;
	private static final float SCALING_FACTOR_STACK_LAYER = 0.5f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.05f;
	
	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 900;
	
	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	private JukeboxHierarchyLayer transitionLayer;
	
	private ArrayList<SlerpAction> arSlerpActions;
	private Time time;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;
	
	private GLConnectionLineRenderer glConnectionLineRenderer;
	
	private int iDraggedViewID = -1;
	
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
		
		transitionLayer = new JukeboxHierarchyLayer(generalManager,
				1, 
				SCALING_FACTOR_TRANSITION_LAYER, 
				null);
		
		underInteractionLayer.setParentLayer(stackLayer);
		stackLayer.setChildLayer(underInteractionLayer);
		stackLayer.setParentLayer(poolLayer);
		poolLayer.setChildLayer(stackLayer);
		
		Transform transformUnderInteraction = new Transform();
		transformUnderInteraction.setTranslation(new Vec3f(0, 0, 0f));
		transformUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));

		underInteractionLayer.setTransformByPositionIndex(0, transformUnderInteraction);
		
		Transform transformTransition = new Transform();
		transformTransition.setTranslation(new Vec3f(0, 0, 0.1f));
		transformTransition.setScale(new Vec3f(
				SCALING_FACTOR_TRANSITION_LAYER,
				SCALING_FACTOR_TRANSITION_LAYER,
				SCALING_FACTOR_TRANSITION_LAYER));
		
		transitionLayer.setTransformByPositionIndex(0, transformTransition);
		
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
	    
		retrieveContainedViews(gl);
		
	    buildPoolLayer(gl);
		buildStackLayer(gl);
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
		renderLayer(gl, transitionLayer);
		renderLayer(gl, poolLayer);
		renderLayer(gl, stackLayer);
		renderLayer(gl, underInteractionLayer);
//		renderUnderInteractionLayer(gl);
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
			
			if (underInteractionLayer.getElementList().size() < underInteractionLayer.getCapacity())
			{
				underInteractionLayer.addElement(iViewId);
				underInteractionLayer.setElementVisibilityById(true, iViewId);
			}
			else if (stackLayer.getElementList().size() < stackLayer.getCapacity())
			{
				stackLayer.addElement(iViewId);
				stackLayer.setElementVisibilityById(true, iViewId);
			}
			else if (poolLayer.getElementList().size() < poolLayer.getCapacity())
			{
				poolLayer.addElement(iViewId);
				poolLayer.setElementVisibilityById(true, iViewId);
			}
			
			tmpGLEventListener.initRemote(gl, iUniqueId, stackLayer, pickingTriggerMouseAdapter);
			
			pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_SELECTION, iViewId);
		}
	}
	
	private void buildStackLayer(final GL gl) {
		
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
	
	private void buildPoolLayer(final GL gl) {
		
		for (int iViewIndex = 0; iViewIndex < poolLayer.getCapacity(); iViewIndex++)
		{		
			Transform transform = new Transform();
			transform.setTranslation(new Vec3f(4.1f, 
					0.5f * iViewIndex, 4));
			transform.setScale(new Vec3f(SCALING_FACTOR_POOL_LAYER,
					SCALING_FACTOR_POOL_LAYER,
					SCALING_FACTOR_POOL_LAYER));		
			poolLayer.setTransformByPositionIndex(iViewIndex, transform);		}
	}

		
	private void renderBucketWall(final GL gl) {
//		
//		gl.glColor3f(0.4f, 0.4f, 0.4f);
//		gl.glLineWidth(3);
//		
//		gl.glBegin(GL.GL_LINE_LOOP);
//		gl.glVertex3f(0, 0, -0.01f);
//		gl.glVertex3f(0, 8, -0.01f);
//		gl.glVertex3f(8, 8, -0.01f);
//		gl.glVertex3f(8, 0, -0.01f);
//		gl.glEnd();
	}
	
//	private void renderUnderInteractionLayer(final GL gl) {
//		
//		// Check if a pathway is currently under interaction
//		if (underInteractionLayer.getElementList().size() == 0)
//			return;
//
//		int iViewId = underInteractionLayer.getElementIdByPositionIndex(0);
//		
//		// Check if pathway is valid
//		if(iViewId == -1)
//			return;
//		
//		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_SELECTION, iViewId));
//		renderViewById(gl, iViewId, underInteractionLayer);
//		gl.glPopName();
//	}
	
	private void renderLayer(final GL gl, 
			final JukeboxHierarchyLayer layer) {

		Iterator<Integer> iterElementList = layer.getElementList().iterator();
		int iViewId = 0;
		
		while(iterElementList.hasNext())
		{		
			iViewId = iterElementList.next();		
			
			// Check if pathway is valid
			if(iViewId == -1)
				continue;
			
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_SELECTION, iViewId));
			renderViewById(gl, iViewId, layer);		
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
		
		SlerpAction tmpSlerpAction = arSlerpActions.get(0);
		
		if (iSlerpFactor == 0)
		{
			tmpSlerpAction.start();
			
			int iOriginPosIndex;
			if (tmpSlerpAction.getOriginHierarchyLayer().equals(transitionLayer))
				iOriginPosIndex = 0;
			else
				iOriginPosIndex = tmpSlerpAction.getOriginPosIndex();
			
			tmpSlerpAction.getOriginHierarchyLayer().setElementVisibilityById(false, 
					tmpSlerpAction.getOriginHierarchyLayer().getElementIdByPositionIndex(
							iOriginPosIndex));
		}
		
		if (iSlerpFactor < SLERP_RANGE)
		{
			// Makes animation rendering speed independent
			iSlerpFactor += SLERP_SPEED * time.deltaT();
			
			if (iSlerpFactor > SLERP_RANGE)
				iSlerpFactor = SLERP_RANGE;
		}
		
		slerpView(gl, tmpSlerpAction);
	}
	
	private void slerpView(final GL gl, SlerpAction slerpAction) {

		int iViewId = slerpAction.getElementId();
		SlerpMod slerpMod = new SlerpMod();
		
		if ((iSlerpFactor == 0))
		{	
			slerpMod.playSlerpSound();
		}
		
		int iOriginPosIndex;
		if (slerpAction.getOriginHierarchyLayer().equals(transitionLayer))
			iOriginPosIndex = 0;
		else
			iOriginPosIndex = slerpAction.getOriginPosIndex();
		
		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginHierarchyLayer().getTransformByPositionIndex(
						iOriginPosIndex), slerpAction
				.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()),
				(float)iSlerpFactor / SLERP_RANGE);

		gl.glPushMatrix();
		
		slerpMod.applySlerp(gl, transform);
		
		((AGLCanvasUser) generalManager.getSingelton()
				.getViewGLCanvasManager().getItem(iViewId)).displayRemote(gl);

		gl.glPopMatrix();

		if (iSlerpFactor >= SLERP_RANGE)
		{
			slerpAction.getDestinationHierarchyLayer()
					.setElementVisibilityById(true, iViewId);

			arSlerpActions.remove(slerpAction);
			
			// Remove view from origin layer after slerping
			slerpAction.getOriginHierarchyLayer().removeElement(
					slerpAction.getOriginPosIndex());
			
			iSlerpFactor = 0;
		}
	}
	
	protected void checkForHits() 
	{
		ArrayList<Pick> alHits = null;		
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.VIEW_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iViewID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:			
					
							if (iDraggedViewID == -1 || iViewID == iDraggedViewID)
							{
								pickingManager.flushHits(iUniqueId, EPickingType.VIEW_SELECTION);
								return;
							}
							
							// TODO: Should be done using slerping
							if (stackLayer.containsElement(iViewID))
							{
								int iDestPositionIndex = stackLayer.getPositionIndexByElementId(iViewID);
								
								if (stackLayer.containsElement(iDraggedViewID))
								{
									int iSrcPositionIndex = stackLayer.getPositionIndexByElementId(iDraggedViewID);
									stackLayer.setElementByPositionIndex(iSrcPositionIndex, iViewID);
								}		
								else if (underInteractionLayer.containsElement(iDraggedViewID))
								{
									int iSrcPositionIndex = underInteractionLayer.getPositionIndexByElementId(iDraggedViewID);
									underInteractionLayer.setElementByPositionIndex(iSrcPositionIndex, iViewID);
								}
								
								stackLayer.setElementByPositionIndex(iDestPositionIndex, iDraggedViewID);
							}		
							else if (underInteractionLayer.containsElement(iViewID))
							{
								int iDestPositionIndex = underInteractionLayer.getPositionIndexByElementId(iViewID);
								
								if (stackLayer.containsElement(iDraggedViewID))
								{
									int iSrcPositionIndex = stackLayer.getPositionIndexByElementId(iDraggedViewID);
									stackLayer.setElementByPositionIndex(iSrcPositionIndex, iViewID);
								}		
								
								underInteractionLayer.setElementByPositionIndex(iDestPositionIndex, iDraggedViewID);
							}	
							
							// Reset dragged view
							iDraggedViewID = -1;
							
							break;
					}
				}
			}
		}	
		
		if (iDraggedViewID != -1)
		{
			pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_MOVE_HIERARCHY_UP_ICON_SELECTION);
			pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_REMOVE_ICON_SELECTION);
			pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_SWITCH_ICON_SELECTION);
			pickingManager.flushHits(iUniqueId, EPickingType.VIEW_SELECTION);
			return;
		}
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.BUCKET_MOVE_HIERARCHY_UP_ICON_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:
							loadViewToUnderInteractionLayer(iExternalID);
							break;
					}
				}
			}
		}	
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.BUCKET_REMOVE_ICON_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:

							if (underInteractionLayer.containsElement(iExternalID))
								underInteractionLayer.removeElement(iExternalID);
							if (stackLayer.containsElement(iExternalID))
								stackLayer.removeElement(iExternalID);
							
							break;
					}
				}
			}
		}	
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.BUCKET_SWITCH_ICON_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:

							iDraggedViewID = iExternalID;	
							
							break;
					}
				}
			}
		}	
		
		pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_MOVE_HIERARCHY_UP_ICON_SELECTION);
		pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_REMOVE_ICON_SELECTION);
		pickingManager.flushHits(iUniqueId, EPickingType.BUCKET_SWITCH_ICON_SELECTION);
		pickingManager.flushHits(iUniqueId, EPickingType.VIEW_SELECTION);
	}
	
	private void loadViewToUnderInteractionLayer(final int iViewID) {

		generalManager
				.getSingelton()
				.logMsg(this.getClass().getSimpleName()
								+ ": loadPathwayToUnderInteractionPosition(): View with ID "
								+ iViewID + " is under interaction.", LoggerType.VERBOSE);

		// Check if pathway is already under interaction
		if (underInteractionLayer.containsElement(iViewID))
			return;
				
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
			return;

		arSlerpActions.clear();

//		// Check if under interaction layer is free
//		if (underInteractionLayer.getElementList().isEmpty())
//		{
//			// Slerp directly from pool to under interaction layer
//			SlerpAction slerpAction = new SlerpAction(iViewId, stackLayer, false);
//			arSlerpActions.add(slerpAction);
//		}
//		else
//		{

		// Check if layered layer has free spot to switch out view under interaction
		if (stackLayer.getElementList().size() < stackLayer.getCapacity())
		{
			// Slerp current view back to layered view
			if (!underInteractionLayer.getElementList().isEmpty())
			{
				SlerpAction reverseSlerpAction = new SlerpAction(
						underInteractionLayer.getElementIdByPositionIndex(0),
						underInteractionLayer, true);
	
				arSlerpActions.add(reverseSlerpAction);
			}
		}
		else
		{
			// Check if stack layer has a free spot
			if (stackLayer.getElementList().size() < stackLayer.getCapacity())
			{
			}
			else
			{
				// Slerp selected view to under interaction transition position
				SlerpAction slerpActionTransition = new SlerpAction(
						iViewID, poolLayer, transitionLayer);	
				arSlerpActions.add(slerpActionTransition);
				
				// Slerp view from stack to pool
				SlerpAction reverseSlerpAction = new SlerpAction(
						stackLayer.getElementIdByPositionIndex(stackLayer.getNextPositionIndex()),
						stackLayer, true);
				arSlerpActions.add(reverseSlerpAction);		
				
				// Slerp under interaction view to free spot in stack
				SlerpAction reverseSlerpAction2 = new SlerpAction(
						underInteractionLayer.getElementIdByPositionIndex(0),
						underInteractionLayer, true);
				arSlerpActions.add(reverseSlerpAction2);		
				
				// Slerp selected view to under interaction position
				SlerpAction slerpAction = new SlerpAction(
						iViewID, transitionLayer, underInteractionLayer);	
				arSlerpActions.add(slerpAction);
			}
		}
		
		iSlerpFactor = 0;
		
//		// Slerp current pathway back to layered view
//		if (!underInteractionLayer.getElementList().isEmpty())
//		{
//			SlerpAction reverseSlerpAction = new SlerpAction(
//					underInteractionLayer.getElementIdByPositionIndex(0),
//					underInteractionLayer, true);
//
//			arSlerpActions.add(reverseSlerpAction);
//		}
//
//		SlerpAction slerpAction;
//
//		// Prevent slerp action if pathway is already in layered view
//		if (!stackLayer.containsElement(iViewId))
//		{
//			// Slerp to layered pathway view
//			slerpAction = new SlerpAction(iViewId, poolLayer, false);
//
//			arSlerpActions.add(slerpAction);
//		}
//
//		// Slerp from layered to under interaction position
//		slerpAction = new SlerpAction(iViewId, stackLayer, false);
//
//		arSlerpActions.add(slerpAction);
//		iSlerpFactor = 0;
//
////		bRebuildVisiblePathwayDisplayLists = true;
//		//selectedVertex = null;
	}

	@Override
	public void updateReceiver(Object eventTrigger) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.geneview.core.manager.view.EPickingType, org.geneview.core.manager.view.EPickingMode, int, org.geneview.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
		
	}
}
