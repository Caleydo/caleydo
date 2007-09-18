/**
 * 
 */
package cerberus.view.manager.jogl.swing.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;

/**
 * @author Michael Kalkusch
 *
 */
public class SwingJoglWindowAdapter extends WindowAdapter {

	private final int iFrameId;
	
	private final CanvasSwingJoglManager refFrameManager;
	 
	/**
	 * 
	 */
	public SwingJoglWindowAdapter( final CanvasSwingJoglManager refFrameManager,
			final int iFrameId) {
		super();
		
		this.iFrameId = iFrameId;
		this.refFrameManager = refFrameManager;
	}
	
	public void windowClosing(WindowEvent e) {
		refFrameManager.unregisterItem(iFrameId,null);
		refFrameManager.runExit();
	}

}
