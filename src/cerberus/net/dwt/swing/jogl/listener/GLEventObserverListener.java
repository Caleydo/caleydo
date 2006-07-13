/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl.listener;

import cerberus.net.dwt.swing.jogl.listener.GLEventListenerTarget;

/**
 * Observer for GLEventListener to register GLEventListenerTarget.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.net.dwt.swing.jogl.listener.GLEventListenerTarget
 * 
 */
public interface GLEventObserverListener {

	/**
	 * Registers a source. If the source has already been registered it is not added a second time.
	 * 
	 * @param refSource source to be registered
	 */
	public void registerSource( final GLEventListenerTarget refSource );
	
	/**
	 * Unregisters a source.
	 * 
	 * @param refSource source to be unregisterd
	 * @return TRUE if source was in list and removed, FALSE is source was not in list
	 */
	public boolean unregisterSource( final GLEventListenerTarget refSource );
}
