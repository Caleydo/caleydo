/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.xml;

import cerberus.data.UniqueInterface;
import cerberus.data.xml.MementoXML;

/**
 * Interface for one item.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementoCallbackXML
 * @see prometheus.data.xml.MementoNetEventXML
 * 
 */
public interface MementoItemXML 
extends MementoXML, UniqueInterface {

	
	/**
	 * Creates a memento containing all infomation for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXML();
	
}
