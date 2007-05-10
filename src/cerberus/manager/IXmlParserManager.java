/**
 * 
 */
package cerberus.manager;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;

import cerberus.xml.parser.handler.IXmlBaseHandler;
import cerberus.xml.parser.handler.IXmlParserHandler;


/**
 * Forwarded/proxy fro several XMLHandlers.
 * 
 * @author Michael Kalkusch
 * 
 * @see org.xml.sax.ContentHandler;
 * @see org.xml.sax.EntityResolver;
 */
public interface IXmlParserManager 
extends IXmlBaseHandler
{

	/**
	 * Returns the reference to the prometheus.app.SingeltonManager.
	 * 
	 * Note: Do not forget to set the reference to the SingeltonManager inside the constructor.
	 *
	 * @param handler register handler to an opening tag.
	 * @param bOpeningTagExistsOnlyOnce Defines if tag may be present several times. If TURE closing tag unregistes handler and calls destroyHandler()
	 *
	 * @return reference to SingeltonManager
	 */
	public IGeneralManager getManager();
	
	/**
	 * Register a SaxHandler by its opening Tag.
	 * Calls getXmlActivationTag() and hasOpeningTagOnlyOnce() for each handler and
	 * registers the handler using this data.
	 * Also calles initHandler() on the new Handler.
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#initHandler()
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#isHandlerDestoryedAfterClosingTag()
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 * 
	 * @param handler register handler to an opening tag.
	 * @param sOpeningAndClosingTag defines opening and closing tag tiggering the handler to become active.
	 * 
	 * @return TRUE if Handler could be register and FALSE if either handler or its associated opening Tag was already registered.
	 */
	public boolean registerAndInitSaxHandler( final IXmlParserHandler handler );		
	
	
//	/**
//	 * Calls cerberus.xml.parser.base.IXmlParserHandler#destroyHandler()
//	 * 
//	 * @see cerberus.xml.parser.handler.IXmlParserHandler#destroyHandler()
//	 * 
//	 * @param handler handel, that should be unregistered.
//	 * 
//	 * @return TRUE if handle was removed.
//	 */
//	public boolean unregisterSaxHandler( final IXmlParserHandler handler );	
	
	/**
	 * Unregister a Handler by its String
	 * 
	 * @param sOpeningAndClosingTag tag to identify handler.
	 * 
	 * @return TRUE if handle was unregistered.
	 */
	public boolean unregisterSaxHandler( final String sActivationXmlTag );	
	
	
	/**
	 * Callback called by cerberus.xml.parser.handler.IXmlParserHandler if clasing tag is read in endElement()
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler
	 * @see orl.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * @param handler calling handler, that just read its closing tag
	 * 
	 */
	public void sectionFinishedByHandler(IXmlParserHandler handler);
	
	/**
	 * Open a new XML file and spart parsing it.
	 * 
	 * @param filename XML file name.
	 * @return true if file existed and was parsed successfully
	 */
	public boolean parseXmlFileByName( String filename );
	
	
	/**
	 * Open a new XML file and start parsing it
	 * 
	 * @param inputStream stream containing an XML file.
	 * @param inputStreamText label only
	 * 
	 * @return true if file existed and was parsed successfully
	 */
	public boolean parseXmlFileByInputStream( InputSource inputStream,
			final String inputStreamText );
	
	
	/**
	 * Cleanup called by Mananger after Handler is not used any more. 
	 */
	public void destroyHandler();
	

	
//	/**
//	 * Special case of recursive xml file parser/reader.
//	 * Attention: beware of side effect due to return value of this method, because
//	 * if TRUE is returened newHandler.startElement( uri,localName,qName,attrib ) and currentHandler.startElement( uri,localName,qName,attrib )
//	 * has top be called!
//	 * IF FALSE is returned only currentHandler.startElement( uri,localName,qName,attrib ) has to be called. 
//	 * This is implemented as a final method inside cerberus.manager.parser.AXmlParserManager#openCurrentTagForRecursiveReader(OpenExternalXmlFileSaxHandler, IXmlParserManager)
//	 * so please derive from cerberus.manager.parser.AXmlParserManager .
//	 *  
//	 * 
//	 * @param newHandler add new recursive reader
//	 * @param refIXmlParserManager retefence to SmlParserManager to ensure, that only this class can call this method!
//	 * @return TRUE indicates that newHandler.startElement( uri,localName,qName,attrib ) and currentHandler.startElement( uri,localName,qName,attrib ) must be called whiel FALSE indicates that only currentHandler.startElement( uri,localName,qName,attrib ) must be called
//	 * 
//	 * @see cerberus.manager.parser.AXmlParserManager#openCurrentTagForRecursiveReader(OpenExternalXmlFileSaxHandler, IXmlParserManager)
//	 */
//	public boolean openCurrentTagForRecursiveReader( 
//			OpenExternalXmlFileSaxHandler newHandler,
//			final IXmlParserManager refIXmlParserManager );
	
	/**
	 * Get the current XmlSaxParser handler or null if no handler ist active.
	 * 
	 * @return reference to current XmlSaxParser handler
	 */
	public IXmlParserHandler getCurrentXmlParserHandler();
	
	
	/**
	 * Call this method if the current tag was not handled by 
	 * endElement(java.lang.String, java.lang.String, java.lang.String)
	 * of cerberus.xml.parser.handler.IXmlParserHandler
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 * 
	 */
	public void endElement_search4Tag(String uri, 
			String localName, 
			String qName);
	
	
	/**
	 * Call this method, if current tag was not handled by 
	 * startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * of cerberus.xml.parser.handler.IXmlParserHandler
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * 
	 */
	public void startElement_search4Tag(String uri, 
			String localName, 
			String qName,
			Attributes attrib);
	
}
