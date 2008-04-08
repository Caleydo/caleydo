/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.xml;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.xml.IMementoXML;

/**
 * Interface for one item.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.IMementoCallbackXML
 * @see prometheus.data.xml.IMementoNetEventXML
 * 
 */
public interface IMementoItemXML 
extends IMementoXML, IUniqueObject {

	
	/**
	 * Creates a memento containing all infomation for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXML();
	
}
