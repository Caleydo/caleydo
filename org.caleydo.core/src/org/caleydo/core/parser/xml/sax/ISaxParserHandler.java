package org.caleydo.core.parser.xml.sax;

import org.xml.sax.Attributes;

/**
 * Interface for all all D*Components parser using SAX.
 * 
 * @author Michael Kalkusch
 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler
 * @see org.xml.sax.helpers.DefaultHandler
 */
public interface ISaxParserHandler
{

	/**
	 * Resets all flags.
	 * 
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#reset()
	 */
	public abstract void reset();

	/**
	 * startElement() for parser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(Stringt, Stringt,
	 *      Stringt, org.xml.sax.Attributes)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#startElement(String,
	 *      String, String, Attributes)
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes
	 *            attributes bound to qName
	 */
	public abstract void startElement(String uri, String localName, String qName,
			Attributes attributes);

	/**
	 * endElement for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(Stringt, Stringt,
	 *      Stringt)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String,
	 *      String, String)
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 */
	public abstract void endElement(String uri, String localName, String qName);

}