/**
 * 
 */
package cerberus.view.gui.awt.jogl;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Michael Kalkusch
 *
 */
public interface IJoglMouseListener 
//extends MouseListener, MouseMotionListener
{

	/**
	 * This sould be "synchronized"
	 * 
	 * @param fView_RotX
	 * @param fView_RotY
	 * @param fViewRotZ
	 */
	 public void setViewAngles( float fView_RotX, 
			 float fView_RotY, 
			 float fViewRotZ );
	 
	 /**
	  * This sould be "synchronized"
	  * 
	  * @param fView_X
	  * @param fView_Y
	  * @param fView_Z
	  */
	 public void setTranslation( float fView_X, float fView_Y, float fView_Z );
	 
}
