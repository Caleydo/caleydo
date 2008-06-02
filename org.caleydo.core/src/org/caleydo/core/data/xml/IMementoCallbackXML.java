package org.caleydo.core.data.xml;

import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

/**
 * Interface for loading and saving DNetEventComponentInterface objects to and from XML file.
 * 
 * @author Michael Kalkusch
 */
public interface IMementoCallbackXML {
	
	/**
	 * Defines a callback, that can be triggered by the parser once a certain tag is reached.
	 * 
	 * Note: this is used to notify the end of a tag in most cases, 
	 * to be able to create an object with the information provided.
	 * 
	 * This callback is triggered via the reference passed to the parser in the constructor.
	 * 
	 * @param type type of object
	 * @param tag_causes_callback Tag that cased the callback
	 * @param details additional information
	 * @param saxHandler SaxHandler with state for object from XML file
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler saxHandler);	
	
}
