/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt;

/**
 * Types of different net events.
 * 
 * @see prometheus.net.dwt.DNetEvent
 * @see prometheus.net.dwt.DNetEventListener
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @author Michael Kalkusch
 *
 */
public enum DNetEventType {

	MOUSE(),
	KEYBOARD(),
	NONE();
	
	// private void NetEventType() { };
}
