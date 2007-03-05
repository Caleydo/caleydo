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

import java.util.Observable;
import java.util.Observer;

import cerberus.data.view.camera.ViewCamera;
import cerberus.data.view.camera.ViewCameraPitchRollYaw;

/**
 * Abstract factory combined with Observer Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public class ViewCameraObservable 
	extends Observable 
	implements ViewCamera, Observer {

	protected ViewCamera refViewCamera;
	
	/**
	 * 
	 */
	public ViewCameraObservable() {
		//super();
		
		refViewCamera = new ViewCameraPitchRollYaw();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraPosition(gleem.linalg.Vec3f)
	 */
	public void setCameraPosition(Vec3f setPos) {
		refViewCamera.setCameraPosition(setPos);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraRotation(gleem.linalg.Rotf)
	 */
	public void setCameraRotation(Rotf setRot) {
		refViewCamera.setCameraRotation( setRot);
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraScale(gleem.linalg.Vec3f)
	 */
	public void setCameraScale(Vec3f setScale) {
		refViewCamera.setCameraScale( setScale);

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setCameraAll(gleem.linalg.Vec3f, gleem.linalg.Vec3f, gleem.linalg.Rotf)
	 */
	public void setCameraAll(Vec3f setPos, Vec3f setScale, Rotf setRot) {
		refViewCamera.setCameraAll( setPos, setScale, setRot);

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#setHasChanged(boolean)
	 */
	public void setHasChanged(boolean bSetHasChanged) {
		refViewCamera.setHasChanged( bSetHasChanged );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getCameraScale()
	 */
	public Vec3f getCameraScale() {
		return refViewCamera.getCameraScale();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getCameraPosition()
	 */
	public Vec3f getCameraPosition() {
		return refViewCamera.getCameraPosition();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getCameraRotation()
	 */
	public Rotf getCameraRotation() {
		return refViewCamera.getCameraRotation();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.view.camera.ViewCamera#getCameraMatrix()
	 */
	public Mat4f getCameraMatrix() {
		return refViewCamera.getCameraMatrix();
	}

	/**
	 * Use this methode to trigger an update.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		assert o == null : "Observable is not null";
		assert arg == null : "Object is not null";
		
		super.notifyObservers();
	}
}
