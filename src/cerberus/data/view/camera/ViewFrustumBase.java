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

import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraBase;


/**
 * @author Michael Kalkusch
 *
 */
public class ViewFrustumBase implements IViewFrustum {

	protected IViewCamera refViewCamera;
	
	protected boolean bViewFrustumHasChanged = true;
	
	protected float fFocalLengthNearPlane = 1.0f;
	
	protected float fFocalLengthFarPlane = 2.0f;
	
	/**
	 * 
	 */
	public ViewFrustumBase() {
		refViewCamera = new ViewCameraBase(99999,this);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasChanged()
	 */
	public boolean hasChanged() {
		if ( bViewFrustumHasChanged ) return true;
		
		return refViewCamera.hasViewCameraChanged();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasFrustumChanged()
	 */
	public boolean hasFrustumChanged() {
		return bViewFrustumHasChanged;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setHasFrustumChanged(boolean)
	 */
	public void setHasFrustumChanged( final boolean bSetHasFrustumChanged ) {
		bViewFrustumHasChanged = bSetHasFrustumChanged;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#getNearPlane()
	 */
	public Plane getNearPlane() {
		assert false : "is not implemented yet";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFarPlane()
	 */
	public Plane getFarPlane() {
		assert false : "is not implemented yet";
	
		return null;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setNearPlane(gleem.linalg.Plane)
	 */
	public void setNearPlane( final Plane setNearPlane) {
		assert false : "is not implemented yet";
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setFarPlane(gleem.linalg.Plane)
	 */
	public void setFarPlane( final Plane setFarPlane) {
		assert false : "is not implemented yet";
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setFocalLength(float, float)
	 */
	public void setFocalLength( final float fFocalLengthNearPlane,
			float fFocalLengthFarPlane) {
		
		this.fFocalLengthNearPlane = fFocalLengthNearPlane;
		this.fFocalLengthFarPlane = fFocalLengthFarPlane;
		
		bViewFrustumHasChanged = true;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFocalLengthNearPlane()
	 */
	public float getFocalLengthNearPlane() {
		return fFocalLengthNearPlane;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFocalLengthFarPlane()
	 */
	public float getFocalLengthFarPlane() {
		return fFocalLengthFarPlane;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#setViewCamera(prometheus.data.collection.view.camera.ViewCamera)
	 */
	public void setViewCamera( final IViewCamera setViewCamera) {
		refViewCamera = setViewCamera;
		
		refViewCamera.setHasChanged( true );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewFrustum#getViewCamera()
	 */
	public IViewCamera getViewCamera() {
		return refViewCamera;
	}

}
