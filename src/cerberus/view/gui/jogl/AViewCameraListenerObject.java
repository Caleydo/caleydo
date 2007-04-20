/**
 * 
 */
package cerberus.view.gui.jogl;

import cerberus.data.AUniqueManagedObject;
import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraBase;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.jogl.IJoglMouseListener;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AViewCameraListenerObject 
extends AUniqueManagedObject
implements IJoglMouseListener {

	protected IViewCamera refViewCamera;
	
	
	/**
	 * Constructor uses refIViewCamera as IViewCamera.
	 * If refViewCamera==null a new cerberus.data.view.camera.ViewCameraBase is created. 
	 * 
	 * @param iUniqueId
	 * @param setGeneralManager
	 * @param refViewCamera
	 */
	protected AViewCameraListenerObject(int iUniqueId,
			final IGeneralManager setGeneralManager,
			final IViewCamera setViewCamera) {

		super(iUniqueId, setGeneralManager);
		
		if ( setViewCamera == null ) {
			this.refViewCamera = new ViewCameraBase(iUniqueId, this);
			return;
		}

		this.refViewCamera = setViewCamera;
		refViewCamera.setCaller(this);
	}

	public final IViewCamera getViewCamera() {

		return refViewCamera;
	}


	public final boolean hasViewCameraChanged() {

		assert refViewCamera != null : "No ViewCamera defefined; null-pointer!";
		
		return refViewCamera.hasViewCameraChanged();
	}


	public final void setViewCamera(IViewCamera set) {

		this.refViewCamera = set;
	}

}
