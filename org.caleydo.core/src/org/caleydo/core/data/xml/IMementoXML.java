/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.xml;

//import java.io.InputStream;
//import org.xml.sax.InputSource;

import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

//import prometheus.net.dwt.DNetEventComponentInterface;

/**
 * Interface for loading and saving DNetEventComponentInterface objects to and from XML file.
 * 
 * @author Michael Kalkusch
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @see prometheus.data.xml.IMementoCallbackXML
 */
public interface IMementoXML {

	/**
	 * Parse the input stream and restore the state defined in the input stream.
	 * 
	 * @param inStream input stream containing XML file.
	 */
	//public void setMementoXML( InputSource inSource );
	
	
	
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler );

}
