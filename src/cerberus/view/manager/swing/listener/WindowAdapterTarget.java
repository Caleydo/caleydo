/**
 * 
 */
package cerberus.view.manager.swing.listener;

/**
 * Define methodes for handling external frames.
 * Is called form windowClosing(WindowEvent).
 * 
 * @author kalkusch
 *
 */
public interface WindowAdapterTarget {

	/**
	 * shut down external frame.
	 * Is called form windowClosing(WindowEvent).
	 *
	 */
	public void windowClosingAction();
	
}
