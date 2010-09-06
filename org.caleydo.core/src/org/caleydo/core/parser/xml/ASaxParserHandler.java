package org.caleydo.core.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Base class for SAX Parser containing several useful methods.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ASaxParserHandler
	extends DefaultHandler {

	/**
	 * Buffer for error messages. An error message always sets the flag
	 * org.caleydo.core.net.dwt.swing.DParseBaseSaxHandler#bErrorWhileParsing true.
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#sInfoMessage
	 */
	private String sErrorMessage;

	/**
	 * Reference to the local Locator
	 */
	private LocatorImpl locator;

	/**
	 * This variabel defines, if a parsing error shall throw a org.xml.sax.SAXParseException. If an parsing
	 * error occurs the variabel org.caleydo.core.net.dwt.swing.DParseBaseSaxHandler#bErrorWhileParsing is set
	 * to true.
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 */
	protected boolean bErrorHaltParsingOnError = true;

	/**
	 * This variable indicates, that an error has occurred. With respect to the variabel
	 * org.caleydo.core.net.dwt.swing.DParseBaseSaxHandler# bError_HaltParsingOnError the parsing is
	 * interrupted.
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#bErrorHaltParsingOnError
	 */
	protected boolean bErrorWhileParsing = false;

	/**
	 * Default Constructor. Sets bEnableHaltOnParsingError = false.
	 */
	public ASaxParserHandler() {

		super();
		reset();
		setSaxHandlerLocator(new LocatorImpl());
	}

	/**
	 * Constructor with bEnableHaltOnParsingError.
	 * 
	 * @param bEnableHaltOnParsingError
	 *            enabels or disables halting on errors
	 */
	public ASaxParserHandler(final boolean bEnableHaltOnParsingError) {

		super();
		reset();
		setSaxHandlerLocator(new LocatorImpl());

		bErrorHaltParsingOnError = bEnableHaltOnParsingError;
	}

	/**
	 * Tells if a parsing error will cause an abortion of parsing.
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#bErrorHaltParsingOnError
	 * @return true if a parsing error will cause an parsing abortion
	 */
	final protected boolean isHaltOnParsingErrorSet() {

		return bErrorHaltParsingOnError;
	}

	/**
	 * Sets the reference to the current Locator
	 * 
	 * @see org.xml.sax.helpers.LocatorImpl
	 * @see org.xml.sax.Locator
	 * @param setLocator
	 */
	final protected void setSaxHandlerLocator(LocatorImpl setLocator) {

		assert setLocator != null : "setSaxHandlerLocator() Error due to null-pointer";

		this.locator = setLocator;
		setDocumentLocator(locator);
	}

	/**
	 * Details on current location during parsing.
	 * 
	 * @return current line and column number
	 */
	final protected String detailsOnLocationInXML() {

		return " line=" + locator.getLineNumber() + ":" + locator.getColumnNumber();
	}

	/**
	 * Append an error message and set the "error-has-occurred"-flag true. In comparision
	 * org.caleydo.core.net.dwt.swing.DParseBaseSaxHandler#appandInfoMsg(String) sets an info message without
	 * setting the "error-has-occurred"-flag true.
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#appandInfoMsg(String)
	 * @param errorMessage
	 *            new error message
	 */
	final protected void appandErrorMsg(final String errorMessage) {

		if (sErrorMessage.length() > 1) {
			sErrorMessage += "\n";
		}
		sErrorMessage += errorMessage + detailsOnLocationInXML();

		if (bErrorHaltParsingOnError) {
			try {
				this.fatalError(new SAXParseException("ParseException due to " + errorMessage, locator));
			}
			catch (SAXException s_e) {
				throw new IllegalStateException(s_e.toString());
			}
		}
		if (!bErrorWhileParsing) {
			bErrorWhileParsing = true;
		}
	}

	/**
	 * Test if parsing was successful. To get error message call
	 * org.caleydo.core.net.dwt.swing.DButtonSaxHandler#getErrorMessage()
	 * 
	 * @return TRUE if an error occurred on parsing.
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 */
	public final boolean hasErrorWhileParsing() {

		return bErrorWhileParsing;
	}

	/**
	 * Resets all flags.
	 * 
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#reset()
	 */
	public void reset() {

		sErrorMessage = "";
		bErrorWhileParsing = false;
	}

	/**
	 * startElement() for parser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#startElement(String, String, String, Attributes)
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes
	 *            attributes bound to qName
	 */
	public abstract void startElement(String uri, String localName, String qName, Attributes attributes);

	/**
	 * endElement for pareser callbacks
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(Stringt, Stringt, Stringt)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String)
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 */
	public abstract void endElement(String uri, String localName, String qName);
}
