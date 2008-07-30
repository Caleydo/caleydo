package org.caleydo.core.data.xml;

import org.caleydo.core.data.IUniqueObject;

/**
 * Interface for one item.
 * 
 * @author Michael Kalkusch
 * 
 */
public interface IMementoItemXML 
extends IMementoXML, IUniqueObject {

	/**
	 * Creates a memento containing all information for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXML();
	
}
