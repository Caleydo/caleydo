package org.caleydo.core.data.xml;

/**
 * Interface for loading and saving DNetEventComponentInterface objects to and
 * from XML file.
 * 
 * @author Michael Kalkusch
 * @see prometheus.net.dwt.DNetEventComponentInterface
 */
public interface IMementoNetEventXML
	extends IMementoXML, IMementoItemXML, IMementoCallbackXML
{

	/**
	 * Creates a memento containing all information for this component.
	 * 
	 * @return String containing a XML definition for this component
	 */
	public String createMementoXMLperObject();
}
