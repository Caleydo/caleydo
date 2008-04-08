package org.geneview.core.data.view.camera;

import org.geneview.core.data.view.camera.ViewFrustumBase.ProjectionMode;


/**
 * Interface for the viewing volume data of an OpenGL view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IViewFrustum {
	
	public ProjectionMode getProjectionMode();
	
	public float getLeft();
	
	public float getRight();
	
	public float getTop();
	
	public float getBottom();
	
	public float getNear();
	
	public float getFar();
	
	public void setLeft(final float fLeft);
	
	public void setRight(final float fRight);

	public void setTop(final float fTop);

	public void setBottom(final float fBottom);

	public void setNear(final float fNear);

	public void setFar(final float fFar);

//	/**
//	 * Returns ture if ViewFrustum or camera view point has changed.
//	 * 
//	 * @return TRUE if either camera view point or ViewFrustum-settings has changed.
//	 * 
//	 * @see prometheus.data.collection.view.camera.IViewFrustum#hasFrustumChanged()
//	 * @see prometheus.data.collection.view.camera.IViewCamera#hasViewCameraChanged()
//	 */
//	public boolean hasChanged();
//	
//	
//	/**
//	 * Returns true if ViewFrustum has changed indepandent of the camera view point.
//	 * 
//	 * @return TURE if the ViewFrustum has changed.
//	 * 
//	 * @see prometheus.data.collection.view.camera.IViewCamera#hasViewCameraChanged()
//	 * @see prometheus.data.collection.view.camera.IViewFrustum#hasViewCameraChanged()
//	 * @see prometheus.data.collection.view.camera.IViewFrustum#setHasFrustumChanged(boolean)
//	 */
//	public boolean hasFrustumChanged();
//	
//	/**
//	 * 
//	 * @param bSetHasFrustumChanged
//	 * 
//	 * @see prometheus.data.collection.view.camera.IViewFrustum#hasFrustumChanged()
//	 */
//	public void setHasFrustumChanged( final boolean bSetHasFrustumChanged );
//	
//	public Plane getNearPlane();
//	
//	public Plane getFarPlane();
//	
//	public void setNearPlane( final Plane setNearPlane );
//	
//	public void setFarPlane( final Plane setFarPlane );
//	
//	public void setFocalLength( final float fFocalLengthNearPlane, 
//			final float fFocalLengthFarPlane );
//
//	
//	public float getFocalLengthNearPlane();
//	
//	public float getFocalLengthFarPlane();
//	
//	
//	public void setViewCamera( final IViewCamera setViewCamera );
//	
//	public IViewCamera getViewCamera();
//	
	
}
