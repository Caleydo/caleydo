/**
 * 
 */
package cerberus.view.manager.swing.listener;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * @author kalkusch
 *
 */
public class InternalFrameAdapterCerberus extends InternalFrameAdapter {

	private final IWindowAdapterTarget target;
	
	/**
	 * 
	 */
	public InternalFrameAdapterCerberus(IWindowAdapterTarget target) {
		this.target = target;
	}
	
	public void internalFrameClosed(InternalFrameEvent e) {
		System.err.println("Close internal AWT-Frame...");
		//target.windowClosingAction();
	}

}
