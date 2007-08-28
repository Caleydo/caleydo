/**
 * 
 */
package cerberus.data.collection.set.viewdata;

import cerberus.data.collection.ISet;
import cerberus.data.view.camera.IViewCamera;
import cerberus.view.jogl.IJoglMouseListener;

/**
 * Create a Set that also is a IJoglMouseListener.
 * IJoglMouseListener provieds acces to cerberus.data.view.camera.IViewCamera
 * 
 * @see cerberus.data.view.camera.IViewCamera
 * @see cerberus.data.collection.SetDataType
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetViewData 
extends ISet, IJoglMouseListener {

	public IViewCamera getViewCamera();
	
	public void setViewCamera( IViewCamera setViewCamera );
	
}
