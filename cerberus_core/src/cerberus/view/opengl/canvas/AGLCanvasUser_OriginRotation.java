/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.collection.set.viewdata.ISetViewData;
import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraBase;
import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 * @see cerberus.view.gui.jogl.IJoglMouseListener
 */
public abstract class AGLCanvasUser_OriginRotation 
extends AGLCanvasUser 
implements IMediatorReceiver
{
	
	/**
	 * @param setGeneralManager
	 */
	protected AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			final IViewCamera refViewCamera,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				refViewCamera,
				iViewId,  
				iParentContainerId, 
				sLabel );		
		
		if ( refViewCamera==null ) {
			IViewCamera newViewCamera = new ViewCameraBase(iViewId,this);
			setViewCamera(newViewCamera);
			
		}
		
		//refLocalViewCamera = refViewCamera;
	}


	
	public final void setOriginRotation( final Vec3f origin,	
		final Rotf rotation ) {
		
		refViewCamera.setCameraPosition(origin);
		refViewCamera.setCameraRotation(rotation);			
	}
	
	public void updateReceiver(Object eventTrigger) {

	}
	
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		if ( updatedSet.getSetType() == SetType.SET_VIEW_DATA ) 
		{
			ISetViewData refSetViewData = (ISetViewData) updatedSet;
			
			refViewCamera.clone(refSetViewData.getViewCamera());
		}
		
	}
}
