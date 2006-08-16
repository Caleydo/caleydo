/**
 * 
 */
package cerberus.xml.parser.manager;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.manager.AXmlParserManager;

/**
 * @author kalkusch
 *
 */
public class XmlParserManager 
extends AXmlParserManager
implements IXmlParserManager
{

	/**
	 * TRUE defines, that handles may be cascaded.
	 * Optimization!
	 */
	protected boolean bUseCascadingHandler = false;
	
	/**
	 * Default constructor.
	 * 
	 * @param generalManager reference to IGeneralManager
	 * @param bUseCascadingHandler TRUE enabeld cascading handlers and slows down parsing speed.
	 */
	public XmlParserManager( final IGeneralManager generalManager,
			final boolean bUseCascadingHandler )
	{
		super( generalManager );
		
		this.bUseCascadingHandler = bUseCascadingHandler;
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public final void startDocument() throws SAXException
	{
		setXmlFileProcessedNow( true );
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public final void endDocument() throws SAXException
	{
		setXmlFileProcessedNow( false );
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attrib) throws SAXException
	{
		if ( currentHandler == null ) {
			if ( hashTag2XmlParser.containsKey( qName ) ) {
				openCurrentTag( hashTag2XmlParser.get( qName ) );
			}
			
			return;
		}
		else 
		{
			if ( bUseCascadingHandler ) {				
				if ( this.hashTag2XmlParser.containsKey( qName ) ) {
					openCurrentTag( hashTag2XmlParser.get( qName ) );
					return;
				}
			}
			
			currentHandler.startElement( uri, 
					localName, 
					qName,
					attrib );
		}

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		if ( currentHandler != null ) {
			if ( sCurrentClosingTag.equals( qName ) ) {
				this.closeCurrentTag();
				return;
			}
			
			currentHandler.endElement( uri, 
					localName,
					qName);
		}

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if ( currentHandler != null ) {				
			currentHandler.characters(ch, start, length);
		}
	}




}
