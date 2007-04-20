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

import cerberus.data.AUniqueItem;
import cerberus.data.view.camera.IViewCamera;
import cerberus.math.MathUtil;

/**
 * @author Michael Kalkusch
 *
 */
public class ViewCameraBase 
extends AUniqueItem
implements IViewCamera {

	private Object caller;
	
	/**
	 * Zoom is equal to scaling in a 4x4 matrix
	 */
	protected Vec3f v3fCameraScale = Vec3f.VEC_NULL ;
	
	/**
	 * Pan used to translate.
	 */
	protected Vec3f v3fCameraPosition = Vec3f.VEC_NULL;
	
	/**
	 * Rotation in euler angles
	 */
	protected Vec3f v3fCameraRotationEuler = Vec3f.VEC_NULL;
	
	/**
	 * Rotation stored as qaternion.
	 */
	protected Rotf  rotfCameraRotation;
	
	/**
	 * Flag indicates update state.
	 */
	protected boolean bHasChanged = true;

	
	/**
	 * Matrix created by pan, zoom and rotation.
	 * Is updated by setter methodes.
	 * 
	 */
	protected Mat4f mat4fCameraViewMatrix;
	
	/**
	 * Constructor.
	 * Sets pan to (0,0,0)
	 * zoom to (1,1,1)
	 * and rotation to (1,0,0,0) using null-vector (0,0,0)
	 */
	public ViewCameraBase( int iId, Object caller ) {
		super( iId );		
		
		rotfCameraRotation = new Rotf();
		
		mat4fCameraViewMatrix = Mat4f.MAT4F_UNITY;		
		
		System.err.println("ViewCameraBase constructor [" + Integer.toString(iId) + "]");
		
		this.setCaller(caller);
	}

//	/**
//	 * Default constructor calls ViewCameraBase(int)
//	 *
//	 */
//	public ViewCameraBase() {
//		this(-1);
//	}
	
	/**
	 * Updates the matrix using Roatation Pan and Zoom. 
	 * Note: Does not influence homogenouse coordinates.	 
	 */
	protected void updateMatrix() {
		mat4fCameraViewMatrix.setRotation( rotfCameraRotation );
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
		mat4fCameraViewMatrix.setTranslation( v3fCameraPosition );
	}
		
	public boolean hasViewCameraChanged() {
		return bHasChanged;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewPan(gleem.linalg.Vec3f)
	 */
	public void setCameraPosition(final Vec3f setPan) {
		System.out.println( "   +-> set camera pos! [" + setPan.toString() + "] id=[" +
				getId() + "] " + caller.toString());
		
		v3fCameraPosition = new Vec3f(setPan);
		mat4fCameraViewMatrix.setTranslation( v3fCameraPosition );
	}

	public void addCameraPosition( final Vec3f setPos ) {
		
		this.v3fCameraPosition.add(setPos);
		mat4fCameraViewMatrix.setTranslation( v3fCameraPosition );
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewRotate(gleem.linalg.Rotf)
	 */
	public void setCameraRotation(final Rotf setRot) {
		rotfCameraRotation = setRot;
		mat4fCameraViewMatrix.setRotation( rotfCameraRotation );
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewZoom(gleem.linalg.Vec3f)
	 */
	public void setCameraScale(final Vec3f setZoom) {
		v3fCameraScale = new Vec3f(setZoom);
		mat4fCameraViewMatrix.setScale( v3fCameraScale );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setViewPanZoomRotate(gleem.linalg.Vec3f, gleem.linalg.Vec3f, gleem.linalg.Rotf)
	 */
	public void setCameraAll(final Vec3f setPan, 
			final Vec3f setZoom, 
			final Rotf setRot) {
		v3fCameraPosition = new Vec3f(setPan);
		v3fCameraScale = new Vec3f(setZoom);
		rotfCameraRotation = new Rotf(setRot);
		updateMatrix();
	}

	public void setHasChanged( final boolean bSetHasChanged ) {
		this.bHasChanged = bSetHasChanged;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewZoom()
	 */
	public final Vec3f getCameraScale() {
		return new Vec3f(v3fCameraScale);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewPan()
	 */
	public final Vec3f getCameraPosition() {
		//return this.v3fCameraPosition;
		
//		System.err.println("ViewCameraBase.getCameraPosition  [" + 
//				Integer.toString(getId()) + "]");
		
		return new Vec3f(v3fCameraPosition);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewRotate()
	 */
	public final Rotf getCameraRotation() {
		return new Rotf(this.rotfCameraRotation);
	}
	
	public final float getCameraRotationGrad(Vec3f axis) {
		return MathUtil.radiant2Grad( rotfCameraRotation.get(axis) );
	}
	
	public final float getCameraRotationRadiant(Vec3f axis) {
		return rotfCameraRotation.get(axis);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getViewMatrix()
	 */
	public final Mat4f getCameraMatrix() {
		updateMatrix();
		
		return new Mat4f(this.mat4fCameraViewMatrix);
	}

	public String toString() {
		return  "p:" + this.v3fCameraPosition.toString() +
			" z:" + this.v3fCameraScale.toString() +
			" r:" + this.rotfCameraRotation.toString();
	}

	public final void setCameraRotationVec3f(final Vec3f setRotVec3f) {

		/**
		 * compute Quaternion from input vector assuming vector Vec3f
		 * describs 3 rotations alpha, betha, gamma 
		 */
		Vec3f helpRot_cos = new Vec3f( (float) Math.cos( (setRotVec3f.x() * 0.5f)),
				(float) Math.cos( (setRotVec3f.y() * 0.5f)),
				(float) Math.cos( (setRotVec3f.z() * 0.5f)));
		Vec3f helpRot_sin = new Vec3f( (float) Math.sin( (setRotVec3f.x() * 0.5f)),
				(float) Math.sin( (setRotVec3f.y() * 0.5f)),
				(float) Math.sin( (setRotVec3f.z() * 0.5f)));
		
		float w = helpRot_cos.x()*helpRot_cos.y()*helpRot_cos.z()
			- helpRot_sin.x()*helpRot_sin.y()*helpRot_sin.z();
		
		rotfCameraRotation.set( 
				new Vec3f( helpRot_cos.x()*helpRot_sin.y()*helpRot_cos.z() +
						helpRot_sin.x()*helpRot_sin.y()*helpRot_sin.z(),
						
						helpRot_sin.x()*helpRot_sin.y()*helpRot_cos.z() -
						helpRot_cos.x()*helpRot_sin.y()*helpRot_sin.z(),
						
						helpRot_sin.x()*helpRot_cos.y()*helpRot_cos.z() +
						helpRot_cos.x()*helpRot_cos.y()*helpRot_sin.z()),
						
						w );
	}
	
	public final void addCameraRotationVec3f(final Vec3f setRotVec3f) {

		assert false : "Not teste yet!";
	
		/**
		 * compute Quaternion from input vector assuming vector Vec3f
		 * describs 3 rotations alpha, betha, gamma 
		 */
		Vec3f helpRot_cos = new Vec3f( (float) Math.cos( (setRotVec3f.x() * 0.5f)),
				(float) Math.cos( (setRotVec3f.y() * 0.5f)),
				(float) Math.cos( (setRotVec3f.z() * 0.5f)));
		Vec3f helpRot_sin = new Vec3f( (float) Math.sin( (setRotVec3f.x() * 0.5f)),
				(float) Math.sin( (setRotVec3f.y() * 0.5f)),
				(float) Math.sin( (setRotVec3f.z() * 0.5f)));
		
		float w = helpRot_cos.x()*helpRot_cos.y()*helpRot_cos.z()
			- helpRot_sin.x()*helpRot_sin.y()*helpRot_sin.z();
		
		Rotf temp = new Rotf();
		temp.set( 
				new Vec3f( helpRot_cos.x()*helpRot_sin.y()*helpRot_cos.z() +
						helpRot_sin.x()*helpRot_sin.y()*helpRot_sin.z(),
						
						helpRot_sin.x()*helpRot_sin.y()*helpRot_cos.z() -
						helpRot_cos.x()*helpRot_sin.y()*helpRot_sin.z(),
						
						helpRot_sin.x()*helpRot_cos.y()*helpRot_cos.z() +
						helpRot_cos.x()*helpRot_cos.y()*helpRot_sin.z()),
						
						w );
		
		rotfCameraRotation.times(temp);
	}

	public final Vec3f getCameraRotationEuler() {

		return v3fCameraRotationEuler;
	}

	public void setCameraRotationEuler(final Vec3f setRotEuler) {

		v3fCameraRotationEuler = setRotEuler;
		
	}
	
	public void addCameraRotationEuler(final Vec3f addRotEuler) {

		v3fCameraRotationEuler.add(addRotEuler);
	}

	public void addCameraRotation(final Rotf setRot) {

		Rotf buffer = rotfCameraRotation.times(setRot);

//		System.out.println( "now =" + 
//				rotfCameraRotation.toString() + 
//				"\n  +  " + 
//				setRot.toString() + 
//				"\n ==> " + 
//				buffer.toString() );c
	
		rotfCameraRotation = buffer;
	}

	public void addCameraScale(final Vec3f setScale) {

		System.err.println("    +--> addCamera() " + this.getId() + "  " + 
				caller.toString() + "  " + caller.getClass().getSimpleName() );
		
		v3fCameraScale.add(setScale);
		v3fCameraPosition.add(setScale);
	}
	
	public final Vec3f addCameraScaleAndGet(final Vec3f setScale) {

		addCameraScale(setScale);
		
		return new Vec3f(v3fCameraScale);
	}

	public void clone(IViewCamera cloneFromCamera) {

		this.v3fCameraPosition = new Vec3f(cloneFromCamera.getCameraPosition());
		this.v3fCameraScale = new Vec3f(cloneFromCamera.getCameraScale());
		this.rotfCameraRotation = new Rotf(cloneFromCamera.getCameraRotation());
		
		bHasChanged = true;
	}

	
	/**
	 * @param caller the caller to set
	 */
	public final void setCaller(Object caller) {
			
		this.caller = caller;
		
		System.err.println("  setCaller " + this.getId() + "  " + 
				caller.toString() + "  " + caller.getClass().getSimpleName() );
	}
}
