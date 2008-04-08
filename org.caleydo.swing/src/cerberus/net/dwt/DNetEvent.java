/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt;

import java.io.Serializable;

import cerberus.net.dwt.DNetEventType;
import cerberus.net.protocol.interaction.SuperMouseEvent;

/**
 * Base interface for all GUI components.
 * 
 * @see prometheus.net.dwt.DNetEventListener
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @author Michael Kalkusch
 *
 */
public interface DNetEvent extends Serializable {

	/**
	 * Get the type of event.
	 * 
	 * @return type of event
	 */
	public DNetEventType getNetEventType();
	
	public SuperMouseEvent getSuperMouseEvent();
	
	public void setSuperMouseEvent( SuperMouseEvent setEvent );
}
