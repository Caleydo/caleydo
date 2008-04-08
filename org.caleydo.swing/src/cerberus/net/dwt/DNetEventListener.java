/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt;

import cerberus.data.xml.IMementoNetEventXML;
import cerberus.net.dwt.DNetEvent;

/**
 * Base interfac for all DNetEvent listener
 * 
 * @see prometheus.net.dwt.DNetEvent
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @author Michael Kalkusch
 *
 */
public interface DNetEventListener 
extends IMementoNetEventXML {

	public void netActionPerformed( DNetEvent event );
	
}
