/**
 * 
 */
package cerberus.xml.parser.handler.importer;

import org.xml.sax.Attributes;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IXmlParserManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.handler.AXmlParserHandler;
import cerberus.xml.parser.handler.IXmlParserHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class OpenExternalXmlFileSaxHandler 
extends AXmlParserHandler
implements IXmlParserHandler
{
	
	public static final String sXML_attribute_target = "target";
	
	/**
	 * @param refGeneralManager
	 * @param refXmlParserManager
	 */
	public OpenExternalXmlFileSaxHandler(IGeneralManager refGeneralManager,
			IXmlParserManager refXmlParserManager )
	{
		super(refGeneralManager, refXmlParserManager);
		
		setXmlActivationTag( "read-xml-file" );	
	}
	
	
	public void reset() {		
		
	}
	
	/**
	 * startElement() for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#startElement(String, String, String, Attributes)
	 * 
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName  @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for  @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes attributes bound to qName
	 */
	public void startElement(String uri, 
			String localName,
			String qName, 
			Attributes attributes) {
		
		if ( qName.equalsIgnoreCase( this.sOpeningTag ) ) 
		{
			String sTargetFileName = "";
			
			try 
			{
				sTargetFileName = 
					attributes.getValue( sXML_attribute_target );
					
				if ( sTargetFileName == null ) {
					throw new CerberusRuntimeException( "no XML-file specified!",
							CerberusExceptionType.SAXPARSER );
				}
				
				/**
				 * Recursion...
				 */
//				refXmlParserManager.parseXmlFileByNameAndHandler( 
//						sTargetFileName, this );				
//				
				refXmlParserManager.parseXmlFileByName( sTargetFileName);
				
			}
			catch ( CerberusRuntimeException cre)
			{
				throw new CerberusRuntimeException( "file [" +
						sTargetFileName + 
						"] could not be loaded! Skip file... (Error=" +
						cre.toString() + ")",
						CerberusExceptionType.SAXPARSER );
			}
				
		
		} // if ( qName.equalsIgnoreCase( this.sOpeningTag ) ) 
		else
		{
			refXmlParserManager.startElement_search4Tag( uri, 
					localName,
					qName, 
					attributes );
		}
	}

	/**
	 * endElement for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String)
	 * 
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName  @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for  @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, 
			String localName, 
			String qName){
		
		if (qName.equals(sOpeningTag)) {	
			refXmlParserManager.sectionFinishedByHandler( this );
		}
	}


}
