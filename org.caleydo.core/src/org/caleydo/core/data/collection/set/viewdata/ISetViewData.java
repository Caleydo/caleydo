/**
 * 
 */
package org.caleydo.core.data.collection.set.viewdata;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.view.opengl.IJoglMouseListener;

/**
 * Create a Set that also is a IJoglMouseListener.
 * IJoglMouseListener provieds acces to org.caleydo.core.data.view.camera.IViewCamera
 * 
 * @see org.caleydo.core.data.view.camera.IViewCamera
 * @see org.caleydo.core.data.collection.SetDataType
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetViewData 
extends ISet, IJoglMouseListener {

	public IViewCamera getViewCamera();
	
	public void setViewCamera( IViewCamera setViewCamera );
	
}
