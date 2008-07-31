package org.caleydo.core.manager;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource; // import org.xml.sax.SAXException;
import org.caleydo.core.parser.xml.sax.handler.IXmlBaseHandler;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;

/**
 * Forwarded/proxy for several XMLHandlers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @see org.xml.sax.ContentHandler;
 * @see org.xml.sax.EntityResolver;
 */
public interface IXmlParserManager
	extends IXmlBaseHandler
{

	/**
	 * Register a SaxHandler by its opening Tag. Calls getXmlActivationTag() and
	 * hasOpeningTagOnlyOnce() for each handler and registers the handler using
	 * this data. Also calls initHandler() on the new Handler.
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#initHandler()
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#isHandlerDestoryedAfterClosingTag()
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#getXmlActivationTag()
	 * @param handler register handler to an opening tag.
	 * @param sOpeningAndClosingTag defines opening and closing tag tiggering
	 *            the handler to become active.
	 * @return TRUE if Handler could be register and FALSE if either handler or
	 *         its associated opening Tag was already registered.
	 */
	public boolean registerAndInitSaxHandler(final IXmlParserHandler handler);

	/**
	 * Unregister a Handler by its String
	 * 
	 * @param sOpeningAndClosingTag tag to identify handler.
	 * @return TRUE if handle was unregistered.
	 */
	public boolean unregisterSaxHandler(final String sActivationXmlTag);

	/**
	 * Callback called by org.caleydo.core.parser.handler.IXmlParserHandler if
	 * closing tag is read in endElement()
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
	 * @see orl.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 * @param handler calling handler, that just read its closing tag
	 */
	public void sectionFinishedByHandler(IXmlParserHandler handler);

	/**
	 * Open a new XML file and spart parsing it.
	 * 
	 * @param filename XML file name.
	 * @return true if file existed and was parsed successfully
	 */
	public boolean parseXmlFileByName(String filename);

	/**
	 * Open a new XML file and start parsing it
	 * 
	 * @param inputStream stream containing an XML file.
	 * @param inputStreamText label only
	 * @return true if file existed and was parsed successfully
	 */
	public boolean parseXmlFileByInputStream(InputSource inputStream,
			final String inputStreamText);

	public boolean parseXmlString(final String sMuddlewareXPath, final String xmlString);

	/**
	 * Cleanup called by Manager after Handler is not used any more.
	 */
	public void destroyHandler();

	/**
	 * Get the current XmlSaxParser handler or null if no handler ist active.
	 * 
	 * @return reference to current XmlSaxParser handler
	 */
	public IXmlParserHandler getCurrentXmlParserHandler();

	/**
	 * Call this method if the current tag was not handled by endElement(String,
	 * String, String) of org.caleydo.core.parser.handler.IXmlParserHandler
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 */
	public void endElementSearch4Tag(String uri, String localName, String qName);

	/**
	 * Call this method, if current tag was not handled by startElement(String,
	 * String, String, org.xml.sax.Attributes) of
	 * org.caleydo.core.parser.handler.IXmlParserHandler
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
	 * @see org.xml.sax.ContentHandler#startElement(Stringt, Stringt, Stringt,
	 *      org.xml.sax.Attributes)
	 */
	public void startElementSearch4Tag(String uri, String localName, String qName,
			Attributes attrib);

}
