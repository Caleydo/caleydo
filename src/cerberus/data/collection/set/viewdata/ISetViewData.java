/**
 * 
 */
package cerberus.data.collection.set.viewdata;

import cerberus.data.collection.ISet;
import cerberus.view.gui.jogl.IJoglMouseListener;

/**
 * Create a Set that also is a IJoglMouseListener.
 * IJoglMouseListener provieds acces to cerberus.data.view.camera.IViewCamera
 * 
 * @see cerberus.data.view.camera.IViewCamera
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISetViewData 
extends ISet, IJoglMouseListener {

}
