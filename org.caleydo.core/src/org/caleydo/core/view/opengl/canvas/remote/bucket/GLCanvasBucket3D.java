package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.canvas.remote.AGLRemoteRendering3D;

/**
 * Implementation of the bucket setup. It supports the user with the ability to
 * navigate and interact with arbitrary views.
 * 
 * @author Marc Streit
 * 
 */
public class GLCanvasBucket3D 
extends AGLRemoteRendering3D
{
	private BucketMouseWheelListener bucketMouseWheelListener;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasBucket3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);

		bucketMouseWheelListener = new BucketMouseWheelListener(this);
		
		// Unregister standard mouse wheel listener
		parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized bucket mouse wheel listener
		parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {

		super.display(gl);

		bucketMouseWheelListener.render();
	}
	
	protected void buildStackLayer(final GL gl) {

		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);

		// TOP BUCKET WALL
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 8 * SCALING_FACTOR_STACK_LAYER, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER, SCALING_FACTOR_STACK_LAYER));
		transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(0, transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER, SCALING_FACTOR_STACK_LAYER));
		transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(2, transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER, SCALING_FACTOR_STACK_LAYER));
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(1, transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(8 * SCALING_FACTOR_STACK_LAYER, 0, 0));
		transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
				SCALING_FACTOR_STACK_LAYER, SCALING_FACTOR_STACK_LAYER));
		transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(3, transform);
	}
}