/**
 * 
 */
package cerberus.data;

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
	 * Constructor, as IViewCamera a new cerberus.data.view.camera.ViewCameraBase is created. 
	 * 
	 * @param iUniqueId
	 * @param setGeneralManager
	 */
	protected AViewCameraListenerObject(int iUniqueId,
			IGeneralManager setGeneralManager) {

		super(iUniqueId, setGeneralManager);
		
		this.refViewCamera = new ViewCameraBase();
	}
	
	/**
	 * Constructor uses refIViewCamera as IViewCamera.
	 * 
	 * @param iUniqueId
	 * @param setGeneralManager
	 * @param refViewCamera
	 */
	protected AViewCameraListenerObject(int iUniqueId,
			final IGeneralManager setGeneralManager,
			IViewCamera setViewCamera) {

		super(iUniqueId, setGeneralManager);
		
		assert setViewCamera != null : "No ViewCamera defefined; null-pointer!";
		this.refViewCamera = setViewCamera;
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
