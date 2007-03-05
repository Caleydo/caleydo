/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.view.camera;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import cerberus.data.view.camera.ViewCamera;

/**
 * @author Michael Kalkusch
 *
 */
public class ViewCameraBase implements ViewCamera {

	/**
	 * Zoom is equal to scaling in a 4x4 matrix
	 */
	protected Vec3f v3fCameraScale = Vec3f.VEC_ONE ;
	
	/**
	 * Pan used to translate.
	 */
	protected Vec3f v3fCameraPosition = Vec3f.VEC_NULL;
	
	/**
	 * Rotation stored as qaternion.
	 */
	protected Rotf  rotfCameraRotation;
	
	/**
	 * Flag indicates update state.
	 */
	protected boolean bHasChanged = true;
	
	
	public boolean hasChanged() {
		return bHasChanged;
	}
	
	
	/**
	 * Matrix created by pan, zoom and rotation.
	 * Is updated ba setter methodes.
	 * 
	 * @see prometheus.app.observer.view.MVC_ViewFrustumBase#updateMatrix()
	 */
	protected Mat4f mat4fCameraViewMatrix;
	
	/**
	 * Constructor.
	 * Sets pan to (0,0,0)
	 * zoom to (1,1,1)
	 * and rotation to (1,0,0,0) using null-vector (0,0,0)
	 */
	public ViewCameraBase() {
		
		rotfCameraRotation = new Rotf();
		
		mat4fCameraViewMatrix = Mat4f.MAT4F_UNITY;		
	}

	/**
	 * Updates the matrix using Roatation Pan and Zoom. 
	 * Note: Does not influence homogenouse coordinates.	 
	 */
	protected void updateMatrix() {
		mat4fCameraViewMatrix.setRotation( rotfCameraRotation );
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
		mat4fCameraViewMatrix.setTranslation( v3fCameraPosition );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewPan(gleem.linalg.Vec3f)
	 */
	public void setCameraPosition(Vec3f setPan) {
		System.out.println( "   +-> set camera pos! [" + setPan.toString()  + "]");
		
		v3fCameraPosition = setPan;
		mat4fCameraViewMatrix.setTranslation( v3fCameraPosition );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewRotate(gleem.linalg.Rotf)
	 */
	public void setCameraRotation(Rotf setRot) {
		rotfCameraRotation = setRot;
		mat4fCameraViewMatrix.setRotation( rotfCameraRotation );
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewZoom(gleem.linalg.Vec3f)
	 */
	public void setCameraScale(Vec3f setZoom) {
		v3fCameraScale = setZoom;
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewPanZoomRotate(gleem.linalg.Vec3f, gleem.linalg.Vec3f, gleem.linalg.Rotf)
	 */
	public void setCameraAll(Vec3f setPan, Vec3f setZoom, Rotf setRot) {
		v3fCameraPosition = setPan;
		v3fCameraScale = setZoom;
		rotfCameraRotation = setRot;
		updateMatrix();
	}

	public void setHasChanged( final boolean bSetHasChanged ) {
		this.bHasChanged = bSetHasChanged;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewZoom()
	 */
	public Vec3f getCameraScale() {
		return this.v3fCameraScale;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewPan()
	 */
	public Vec3f getCameraPosition() {
		return this.v3fCameraPosition;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewRotate()
	 */
	public Rotf getCameraRotation() {
		return this.rotfCameraRotation;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewMatrix()
	 */
	public Mat4f getCameraMatrix() {
		//updateMatrix();
		
		return this.mat4fCameraViewMatrix;
	}

	public String toString() {
		return  "p:" + this.v3fCameraPosition.toString() +
			" z:" + this.v3fCameraScale.toString() +
			" r:" + this.rotfCameraRotation.toString();
	}
}
