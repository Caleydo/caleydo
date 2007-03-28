/**
 * 
 */
package cerberus.view.gui.opengl.canvas;


import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraBase;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasUser_OriginRotation 
extends AGLCanvasUser 
{
	
	
	protected IViewCamera refLocalViewCamera;
	
	/**
	 * @param setGeneralManager
	 */
	public AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			final IViewCamera refViewCamera,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		assert refViewCamera != null : "Can not handle null pointer!";
		
		refLocalViewCamera = refViewCamera;
	}

	
	public AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		this(setGeneralManager,
				new ViewCameraBase(),
				iViewId, 
				iParentContainerId,
				sLabel );
	}
	
	
	public final void setOriginRotation( final Vec3f origin,	
		final Rotf rotation ) {
		
		this.refLocalViewCamera.setCameraPosition(origin);
		this.refLocalViewCamera.setCameraRotation(rotation);			
	}
		
	
	/**
	 * @return the refLocalViewCamera
	 */
	public final IViewCamera getRefLocalViewCamera() {
	
		return refLocalViewCamera;
	}

	
	/**
	 * @param refLocalViewCamera the refLocalViewCamera to set
	 */
	public final void setRefLocalViewCamera(IViewCamera refLocalViewCamera) {
	
		this.refLocalViewCamera = refLocalViewCamera;
	}

}
