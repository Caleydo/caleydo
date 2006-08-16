/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.parser;


import org.xml.sax.Attributes;

import cerberus.data.xml.IMementoNetEventXML;
import cerberus.xml.parser.ISaxParserHandler;


/**
 * Handle parsing of DButton from XML file.
 * 
 * @author Michael Kalkusch
 * 
 */
public final class DButtonSaxHandler 
extends AComponentSaxParserHandler 
implements ISaxParserHandler
{
	
	/**
	 * 
	 */
	public DButtonSaxHandler() {
		super();
	}
	
	/**
	 * 
	 */
	public DButtonSaxHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);		
	}
	
	public DButtonSaxHandler(final IMementoNetEventXML setRefParent) {
		super();
		
		this.refParentMementoCaller = setRefParent;
	}

	/**
	 * Reset state. set component type.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ISaxParserHandler#reset()
	 */
	public void reset() {
		
		super.reset();
		
		sTag_XML_DEvent_type = "DButton";	
	}
	
	
	/**
	 * Handle start tag. Only call super methode.
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, 
			String localName, 
			String qName, 
			Attributes attributes) {
		
		startElement_DComponent( uri, localName, qName, attributes );
		
	} // end startElement(String,Attributes) 
	
	/**
	 * Handle end-Tag. Only call super methode.
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName ) {
		
		endElement_DComponent( uri, localName, qName );
		
	} // end endElement(String,Attributes) 


	
}
