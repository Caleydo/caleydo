package org.caleydo.core.view.opengl.canvas.remote.jukebox;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.canvas.remote.AGLRemoteRendering3D;

/**
 * Implementation of the jukebox setup. It supports the user with the ability to
 * navigate and interact with arbitrary views.
 * 
 * @author Marc Streit
 * 
 */
public class GLCanvasJukebox3D 
extends AGLRemoteRendering3D
{
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasJukebox3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {

		super.display(gl);
	}
	
	protected void buildStackLayer(final GL gl) {
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
			// transform.setTranslation(new Vec3f(-2.7f + fLayerYPos, 1.1f, 0));
			// transform.setRotation(new Rotf(new Vec3f(-0.7f, -1f, 0), fTiltAngleRad));
			transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
			
			stackLayer.setTransformByPositionIndex(iLayerIndex, transform);
	
			fLayerYPos -= 1.3;
		}
	}
}