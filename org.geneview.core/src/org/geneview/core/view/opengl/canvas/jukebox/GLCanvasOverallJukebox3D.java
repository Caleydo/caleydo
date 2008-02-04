package org.geneview.core.view.opengl.canvas.jukebox;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.util.slerp.SlerpAction;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.canvas.pathway.JukeboxHierarchyLayer;

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

	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private ArrayList<SlerpAction> arSlerpActions;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;
	
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
		
		underInteractionLayer = new JukeboxHierarchyLayer(1, 
				SCALING_FACTOR_UNDER_INTERACTION_LAYER, 
				null);
		
		stackLayer = new JukeboxHierarchyLayer(4, 
				SCALING_FACTOR_STACK_LAYER, 
				null);
		
		poolLayer = new JukeboxHierarchyLayer(MAX_LOADED_VIEWS, 
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		
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
	}

	private void retrieveContainedViews(final GL gl) {
		
		Iterator<IGLCanvasUser> iterCanvasUser = 
			refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getAllGLCanvasUsers().iterator();
		
		while(iterCanvasUser.hasNext())
		{
			IGLCanvasUser tmp = iterCanvasUser.next();
			
			if(tmp == this)
				continue;
			
			if (underInteractionLayer.getElementList().size() < 1)
				underInteractionLayer.addElement(tmp.getId());
			
			stackLayer.addElement(tmp.getId());
			
			tmp.initGLCanvas(gl);
		}
	}
	
	private void buildStackLayer(final GL gl) {

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
			transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
			stackLayer.setTransformByPositionIndex(iLayerIndex,
					transform);

			fLayerYPos -= 1f;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) {

//		renderPoolLayer(gl);
		renderStackLayer(gl);
		renderUnderInteractionLayer(gl);
	}
	
	private void renderUnderInteractionLayer(final GL gl) {
		
		// Check if a pathway is currently under interaction
		if (underInteractionLayer.getElementList().size() == 0)
			return;

		int iViewId = underInteractionLayer.getElementIdByPositionIndex(0);
		renderViewById(gl, iViewId, underInteractionLayer);
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
						
			renderViewById(gl, iViewId, stackLayer);		
		}
	}
	
	private void renderViewById(final GL gl,
			final int iViewId, 
			final JukeboxHierarchyLayer layer) {
		
//		// Check if view is visible
//		if(!layer.getElementVisibilityById(iViewId))
//			return;
		
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
}
