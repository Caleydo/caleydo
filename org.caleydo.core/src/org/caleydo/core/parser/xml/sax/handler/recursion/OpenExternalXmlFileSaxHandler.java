/**
 * 
 */
package org.caleydo.core.parser.xml.sax.handler.recursion;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.parser.xml.sax.handler.AXmlParserHandler;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.xml.sax.Attributes;

/**
 * @author Michael Kalkusch
 */
public class OpenExternalXmlFileSaxHandler
	extends AXmlParserHandler
	implements IXmlParserHandler
{

	public static final String sXML_attribute_target = "target";

	/**
	 * @param generalManager
	 * @param xmlParserManager
	 */
	public OpenExternalXmlFileSaxHandler(IGeneralManager generalManager,
			IXmlParserManager xmlParserManager)
	{

		super(generalManager, xmlParserManager);

		setXmlActivationTag("read-xml-file");
	}

	public void reset()
	{

	}

	/**
	 * startElement() for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(Stringt, Stringt,
	 *      Stringt, org.xml.sax.Attributes)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#startElement(String,
	 *      String, String, Attributes)
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes attributes bound to qName
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	{

		if (qName.equalsIgnoreCase(this.sOpeningTag))
		{
			String sTargetFileName = "";

			try
			{
				sTargetFileName = attributes.getValue(sXML_attribute_target);

				if (sTargetFileName == null)
				{
					throw new CaleydoRuntimeException("no XML-file specified!",
							CaleydoRuntimeExceptionType.SAXPARSER);
				}

				/**
				 * Recursion...
				 */
				xmlParserManager.parseXmlFileByName(sTargetFileName);

			}
			catch (CaleydoRuntimeException cre)
			{
				throw new CaleydoRuntimeException(
						"file [" + sTargetFileName
								+ "] could not be loaded! Skip file... (Error="
								+ cre.toString() + ")", CaleydoRuntimeExceptionType.SAXPARSER);
			}

		} // if ( qName.equalsIgnoreCase( this.sOpeningTag ) )
		else
		{
			xmlParserManager.startElementSearch4Tag(uri, localName, qName, attributes);
		}
	}

	/**
	 * endElement for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(Stringt, Stringt,
	 *      Stringt)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String,
	 *      String, String)
	 * @param uri URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String qName)
	{

		if (qName.equals(sOpeningTag))
		{
			xmlParserManager.sectionFinishedByHandler(this);
		}
	}

}
