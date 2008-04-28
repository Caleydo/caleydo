package org.caleydo.core.data.view.camera;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import org.caleydo.core.data.IUniqueObject;

/**
 * View camera interface
 * 
 * @see org.caleydo.core.data.collection.SetDataType
 * @see org.caleydo.core.data.collection.set.viewdata.ISetViewData
 *  
 * @author Michael Kalkusch
 *
 */
public interface IViewCamera 
extends IUniqueObject {

	/**
	 * Returns true if any data of the camera has changed.
	 * 
	 * @return TRUE if camera settings has changed
	 * 
	 * @see prometheus.data.collection.view.camera.IViewCamera#setHasChanged(boolean)
	 * 
	 */
	public boolean hasViewCameraChanged();
	//public boolean hasViewCameraChanged();
	
	/**
	 * Apply a new pan. Updates the ViewMatrix using the
	 * existing Rotation & Zoom.
	 * 
	 * @param setPan new pan settings
	 */
	public void setCameraPosition( final Vec3f setPos );
	
	/**
	 * same as setCameraPosition() but adds current setPos vector.
	 * 
	 * @param setPos increment position.
	 */
	public void addCameraPosition( final Vec3f setPos );
	
	/**
	 * Apply a new rotation. Updates the ViewMatrix using the
	 * existing Pan & Zoom.
	 * 
	 * @param setRot new rotation
	 */
	public void setCameraRotation( final Rotf setRot );
	
	/**
	 * add setRot to current rotation.
	 * 
	 * @param setRot incremental rotation of current rotation
	 */
	public void addCameraRotation( final Rotf setRot );
	
	/**
	 * Set rotation in euler angles.
	 *
	 * @param setRotEuler with euler angles x,y,z
	 */
	public void setCameraRotationEuler( final Vec3f setRotEuler );
	
	/**
	 * Add rotation in euler angles.
	 *
	 * @param addRotEuler with euler angles x,y,z
	 */
	public void addCameraRotationEuler(Vec3f addRotEuler);
	
	/**
	 * Get camera rotation as axis and rotation angle in degrees.
	 * Note: uses org.caleydo.core.math.MathUtil#radiant2Grad(float)
	 * 
	 * @see gleem.linalg.Rotf#get(Vec3f)
	 * @see org.caleydo.core.math.MathUtil#radiant2Grad(float)
	 * 
	 * @param axis 
	 * @return angel of rotation around axis (quaternion)
	 */
	public float getCameraRotationGrad(Vec3f axis);
	
	/**
	 * Get camera rotation as axis and rotation angle in degrees.
	 * 
	 * @see gleem.linalg.Rotf#get(Vec3f)
	 * 
	 * @param axis
	 * @return angel of rotation around axis (quaternion)
	 */
	public float getCameraRotationRadiant(Vec3f axis);
	
	/**
	 * Apply a new rotation using Vec3f. Updates the ViewMatrix using the
	 * existing Pan & Zoom.
	 * 
	 * @param setRot new rotation
	 */
	public void setCameraRotationVec3f( final Vec3f setRotVec3f );
	
	/**
	 * Apply new zooming, wchich is a scaling operation.
	 * Updates the ViewMatrix using the
	 * existing Rotation & Zoom.
	 * 
	 * @param setZoom new zoom values
	 */
	public void setCameraScale( final Vec3f setScale );
	
	/**
	 * same as setCameraScale() but adds the setScale vector to the current vector.
	 * 
	 * @param setScale
	 */
	public void addCameraScale( final Vec3f setScale );
	
	/**
	 * Apply new Pan, Zoom and Roation at once.
	 * Note: Does the same as calling 
	 * prometheus.data.collection.view.camera.ViewCamera#setViewPan(Vec3f)
	 * ,
	 * prometheus.data.collection.view.camera.ViewCamera#setViewRotate(Rotf)
	 * and
	 * prometheus.data.collection.view.camera.ViewCamera#setViewZoom(Vec3f)
	 * .
	 * 
	 * @param setPan new pan values
	 * @param setZoom new zoom/scale values
	 * @param setRot new rotation values
	 * 
	 * @see prometheus.data.collection.view.camera.IViewCamera#setCameraPosition(Vec3f)
	 * @see prometheus.data.collection.view.camera.IViewCamera#setCameraRotation(Rotf)
	 * @see prometheus.data.collection.view.camera.IViewCamera#setCameraScale(Vec3f)
	 */
	public void setCameraAll( final Vec3f setPos,
			final Vec3f setScale,
			final Rotf setRot );
	
	
	/**
	 * 
	 * @param bSetHasChanged status of viewCamera, TURE means status has changed.
	 * 
	 * @see prometheus.data.collection.view.camera.IViewCamera#hasViewCameraChanged()
	 */
	public void setHasChanged( final boolean bSetHasChanged );
	
	
	//public void setViewMatrix( final Mat4f setViewMatrix );	
		
	/**
	 * Get the current Zoom value.
	 * 
	 * @return current Zoom value
	 */
	public Vec3f getCameraScale();
	
	/**
	 * Get the current Pan value.
	 * 
	 * @return current Pan value
	 */
	public Vec3f getCameraPosition();
	
	/**
	 * Get the current Zoom value.
	 * 
	 * @return current Rotate value
	 */
	public Rotf getCameraRotation();
	
	/**
	 * Get rotation in euler angles.
	 * 
	 * @return Vec3f with euler angles x,y,z
	 */
	public Vec3f getCameraRotationEuler();
	
	/**
	 * Get the current ViewMatrix.
	 * Note: This matrix is updated each time a set method is called.
	 * 
	 * @return current view matrix
	 */
	public Mat4f getCameraMatrix();
	
	/**
	 * clone parameters of this camera.
	 * 
	 * @param cloneFromCamera
	 */
	public void clone( IViewCamera cloneFromCamera);
	
	/**
	 * Get debug info as String.
	 * 
	 * @return details in ViewCamera
	 */
	public abstract String toString();
}
