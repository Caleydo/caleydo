/**
 * 
 */
package org.geneview.core.data.collection.set.viewdata;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.view.jogl.IJoglMouseListener;

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
