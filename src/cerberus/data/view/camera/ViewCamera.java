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

/**
 * View camera interface
 * 
 * @author Michael Kalkusch
 *
 */
public interface ViewCamera {

	/**
	 * Returns true if any data of the camera has changed.
	 * 
	 * @return TRUE if camera settings has changed
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCamera#setHasChanged(boolean)
	 * 
	 */
	public boolean hasChanged();
	
	/**
	 * Apply a new pan. Updates the ViewMatrix using the
	 * existing Rotation & Zoom.
	 * 
	 * @param setPan new pan settings
	 */
	public void setCameraPosition( final Vec3f setPos );
	
	/**
	 * Apply a new rotation. Updates the ViewMatrix using the
	 * existing Pan & Zoom.
	 * 
	 * @param setRot new rotation
	 */
	public void setCameraRotation( final Rotf setRot );
	
	/**
	 * Apply new zooming, wchich is a scaling operation.
	 * Updates the ViewMatrix using the
	 * existing Rotation & Zoom.
	 * 
	 * @param setZoom new zoom values
	 */
	public void setCameraScale( final Vec3f setScale );
	
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
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraPosition(Vec3f)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraRotation(Rotf)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraScale(Vec3f)
	 */
	public void setCameraAll( final Vec3f setPos,
			final Vec3f setScale,
			final Rotf setRot );
	
	
	/**
	 * 
	 * @param bSetHasChanged status of viewCamera, TURE means status has changed.
	 * 
	 * @see prometheus.data.collection.view.camera.ViewCamera#hasChanged()
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
	 * Get the current ViewMatrix.
	 * Note: This matrix is updated each time a set methode is called.
	 * 
	 * @return current view matrix
	 */
	public Mat4f getCameraMatrix();
	
	/**
	 * Get debug info as String.
	 * 
	 * @return details in ViewCamera
	 */
	public abstract String toString();
}
