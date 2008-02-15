package org.geneview.core.data.view.camera;

/**
 * Defines viewing volume of a OpenGL view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class ViewFrustumBase 
implements IViewFrustum {
	
	public enum ProjectionMode {
		
		ORTHOGRAPHIC,
		PERSPECTIVE
	};
	
	private ProjectionMode projectionMode;
	
	private float fLeft;
	private float fRight;
	private float fTop;
	private float fBottom;
	
	private float fNear;
	private float fFar;
	
	
	/**
	 * Constructor.
	 */
	public ViewFrustumBase(ProjectionMode projectionMode,
			float fLeft,
			float fRight,
			float fBottom,
			float fTop,
			float fNear,
			float fFar) {
		
		this.projectionMode = projectionMode;
		
		this.fLeft = fLeft;
		this.fRight = fRight;
		this.fBottom = fBottom;
		this.fTop = fTop;
		this.fNear = fNear;
		this.fFar = fFar;
	}
	
	public ProjectionMode getProjectionMode() {
		return projectionMode;
	}
	
	public float getLeft() {
		return fLeft;
	}
	
	public float getRight() {
		return fRight;
	}
	
	public float getTop() {
		return fTop;
	}
	
	public float getBottom() {
		return fBottom;
	}
	
	public float getNear() {
		return fNear;
	}
	
	public float getFar() {
		return fFar;
	}
	
//	protected IViewCamera refViewCamera;
//	
//	protected boolean bViewFrustumHasChanged = true;
//	
//	protected float fFocalLengthNearPlane = 1.0f;
//	
//	protected float fFocalLengthFarPlane = 2.0f;
//	
//	/**
//	 * 
//	 */
//	public ViewFrustumBase() {
//		refViewCamera = new ViewCameraBase(99999);
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasChanged()
//	 */
//	public boolean hasChanged() {
//		if ( bViewFrustumHasChanged ) return true;
//		
//		return refViewCamera.hasViewCameraChanged();
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#hasFrustumChanged()
//	 */
//	public boolean hasFrustumChanged() {
//		return bViewFrustumHasChanged;
//	}
//	
//	/*
//	 *  (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#setHasFrustumChanged(boolean)
//	 */
//	public void setHasFrustumChanged( final boolean bSetHasFrustumChanged ) {
//		bViewFrustumHasChanged = bSetHasFrustumChanged;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#getNearPlane()
//	 */
//	public Plane getNearPlane() {
//		assert false : "is not implemented yet";
//	
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFarPlane()
//	 */
//	public Plane getFarPlane() {
//		assert false : "is not implemented yet";
//	
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#setNearPlane(gleem.linalg.Plane)
//	 */
//	public void setNearPlane( final Plane setNearPlane) {
//		assert false : "is not implemented yet";
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#setFarPlane(gleem.linalg.Plane)
//	 */
//	public void setFarPlane( final Plane setFarPlane) {
//		assert false : "is not implemented yet";
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#setFocalLength(float, float)
//	 */
//	public void setFocalLength( final float fFocalLengthNearPlane,
//			float fFocalLengthFarPlane) {
//		
//		this.fFocalLengthNearPlane = fFocalLengthNearPlane;
//		this.fFocalLengthFarPlane = fFocalLengthFarPlane;
//		
//		bViewFrustumHasChanged = true;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFocalLengthNearPlane()
//	 */
//	public float getFocalLengthNearPlane() {
//		return fFocalLengthNearPlane;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#getFocalLengthFarPlane()
//	 */
//	public float getFocalLengthFarPlane() {
//		return fFocalLengthFarPlane;
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#setViewCamera(prometheus.data.collection.view.camera.ViewCamera)
//	 */
//	public void setViewCamera( final IViewCamera setViewCamera) {
//		refViewCamera = setViewCamera;
//		
//		refViewCamera.setHasChanged( true );
//	}
//
//	/* (non-Javadoc)
//	 * @see prometheus.data.collection.view.camera.ViewFrustum#getViewCamera()
//	 */
//	public IViewCamera getViewCamera() {
//		return refViewCamera;
//	}

}
