/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.view.camera;

import gleem.linalg.Plane;

import cerberus.data.view.camera.ViewCamera;

/**
 * @author Michael Kalkusch
 *
 */
public interface ViewFrustum {
	
	/**
	 * Returns ture if ViewFrustum or camera view point has changed.
	 * 
	 * @return TRUE if either camera view point or ViewFrustum-settings has changed.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasFrustumChanged()
	 * @see prometheus.data.collection.view.camera.ViewCamera#hasChanged()
	 */
	public boolean hasChanged();
	
	
	/**
	 * Returns true if ViewFrustum has changed indepandent of the camera view point.
	 * 
	 * @return TURE if the ViewFrustum has changed.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCamera#hasChanged()
	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasChanged()
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setHasFrustumChanged(boolean)
	 */
	public boolean hasFrustumChanged();
	
	/**
	 * 
	 * @param bSetHasFrustumChanged
	 * 
	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasFrustumChanged()
	 */
	public void setHasFrustumChanged( final boolean bSetHasFrustumChanged );
	
	public Plane getNearPlane();
	
	public Plane getFarPlane();
	
	public void setNearPlane( final Plane setNearPlane );
	
	public void setFarPlane( final Plane setFarPlane );
	
	public void setFocalLength( final float fFocalLengthNearPlane, 
			final float fFocalLengthFarPlane );

	
	public float getFocalLengthNearPlane();
	
	public float getFocalLengthFarPlane();
	
	
	public void setViewCamera( final ViewCamera setViewCamera );
	
	public ViewCamera getViewCamera();
	
	
}
