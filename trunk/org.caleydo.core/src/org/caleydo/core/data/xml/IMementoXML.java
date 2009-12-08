package org.caleydo.core.data.xml;

import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

/**
 * Interface for loading and saving DNetEventComponentInterface objects to and from XML file.
 * 
 * @author Michael Kalkusch
 */
public interface IMementoXML {

	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param saxHandler
	 *            reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler(final ISaxParserHandler saxHandler);
}
