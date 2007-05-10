/**
 * 
 */
package cerberus.view.manager.jogl.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cerberus.view.manager.tester.SingeltonJoglFrameManager;

/**
 * @author Michael Kalkusch
 *
 */
public class SingeltonJoglWindowAdapter extends WindowAdapter {

	private final int iFrameId;
	
	private final SingeltonJoglFrameManager refFrameManager;
	 
	/**
	 * 
	 */
	public SingeltonJoglWindowAdapter( final SingeltonJoglFrameManager refFrameManager,
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
