package cerberus.xml.parser;

import org.xml.sax.Attributes;


/**
 * Iterface for all all D*Components parser using SAX.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler
 * @see org.xml.sax.helpers.DefaultHandler
 */
public interface DParseSaxHandler {

	
	/**
	 * Resets all flags.
	 * 
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#reset()
	 *
	 */
	public abstract void reset();

	/**
	 * startElement() for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#startElement(String, String, String, Attributes)
	 * 
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName  @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for  @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes attributes bound to qName
	 */
	public abstract void startElement(String uri, String localName,
			String qName, Attributes attributes);

	/**
	 * endElement for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#endElement(String, String, String)
	 * 
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName  @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for  @see org.xml.sax.helpers.DefaultHandler
	 */
	public abstract void endElement(String uri, String localName, String qName);

}