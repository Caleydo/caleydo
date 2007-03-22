/**
 * 
 */
package cerberus.view.gui.jogl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;


/**
 * @author Michael Kalkusch
 *
 */
public class JoglCanvasTextureForwarder 
extends AViewCameraListenerObject
implements GLEventListener, IJoglMouseListener {

	/**
	 * @param iUniqueId
	 * @param setGeneralManager
	 */
	public JoglCanvasTextureForwarder(int iUniqueId,
			IGeneralManager setGeneralManager) {

		super(iUniqueId, setGeneralManager);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iUniqueId
	 * @param setGeneralManager
	 * @param setViewCamera
	 */
	public JoglCanvasTextureForwarder(int iUniqueId,
			IGeneralManager setGeneralManager,
			IViewCamera setViewCamera) {

		super(iUniqueId, setGeneralManager, setViewCamera);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cerberus.data.IUniqueManagedObject#getBaseType()
	 */
	public ManagerObjectType getBaseType() {

		// TODO Auto-generated method stub
		return null;
	}

	public void display(GLAutoDrawable drawable) {

		// TODO Auto-generated method stub
		
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}

	public void init(GLAutoDrawable drawable) {
		
		//TODO init and laod texture
		
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		// TODO Auto-generated method stub
		
	}

}
