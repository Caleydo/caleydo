/**
 * 
 */
package org.caleydo.core.view.opengl.mouse;

import org.caleydo.core.data.view.camera.IViewCamera;

// import java.awt.event.MouseListener;
// import java.awt.event.MouseMotionListener;

/**
 * @author Michael Kalkusch
 */
public interface IJoglMouseListener
// extends MouseListener, MouseMotionListener
{

	// /**
	// * This sould be "synchronized"
	// *
	// * @deprecated
	// *
	// * @param fView_RotX
	// * @param fView_RotY
	// * @param fViewRotZ
	// */
	// public void setViewAngles( float fView_RotX,
	// float fView_RotY,
	// float fViewRotZ );
	//	 
	// /**
	// * This sould be "synchronized"
	// *
	// * @deprecated
	// *
	// * @param fView_X
	// * @param fView_Y
	// * @param fView_Z
	// */
	// public void setTranslation( float fView_X,
	// float fView_Y,
	// float fView_Z );

	/**
	 * Set the current IViewCamera
	 * 
	 * @param set
	 */
	public void setViewCamera(IViewCamera set);

	/**
	 * Get the current IViewCamera
	 * 
	 * @return
	 */
	public IViewCamera getViewCamera();

	/**
	 * Returns true if any data of the camera has changed.
	 * 
	 * @see org.caleydo.core.data.view.camera.IViewCamera#hasViewCameraChanged()
	 * @return
	 */
	public boolean hasViewCameraChanged();

}
