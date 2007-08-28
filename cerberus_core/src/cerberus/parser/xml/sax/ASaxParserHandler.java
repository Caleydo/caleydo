/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.parser.xml.sax;

//import java.lang.NullPointerException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import cerberus.data.xml.IMementoCallbackXML;
import cerberus.util.exception.GeneViewRuntimeExceptionType;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Base class for SAX Parser containing several useful methods.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ASaxParserHandler 
extends DefaultHandler 
implements ISaxParserHandler 
{

	/**
	 * Buffer for error messages.
	 * An error message always sets the flag cerberus.net.dwt.swing.DParseBaseSaxHandler#bErrorWhileParsing true.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#sInfoMessage
	 */
	private String sErrorMessage;
	
	/**
	 * Buffer for info messages.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#sErrorMessage
	 */
	private String sInfoMessage;
	
	/**
	 * Reference to the local Locator
	 */
	private LocatorImpl refLocator;
	
	
	/**
	 * Reference to the calling MenmentoXML object.
	 */
	protected IMementoCallbackXML refParentMementoCaller = null;
	
	/**
	 * This variabel defines, if a parsing error shall throw a org.xml.sax.SAXParseException.
	 * If an parsing error occurs the variabel cerberus.net.dwt.swing.DParseBaseSaxHandler#bErrorWhileParsing
	 * is set to true. 
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 */
	protected boolean bError_HaltParsingOnError = true;
	
	/**
	 * This variable indicates, that an error has occurred.
	 * With respect to the variabel cerberus.net.dwt.swing.DParseBaseSaxHandler#bError_HaltParsingOnError 
	 * the parsing is interrupted.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bError_HaltParsingOnError 
	 */
	protected boolean bErrorWhileParsing = false;
	

	
	/**
	 * Default Constructor. 
	 * Sets bEnableHaltOnParsingError = false.
	 * 
	 */
	public ASaxParserHandler() {
		super();
		reset();	
		setSaxHandlerLocator( new LocatorImpl() );
	}
	
	/**
	 * Constructor with bEnableHaltOnParsingError.
	 * 
	 * @param bEnableHaltOnParsingError enabels or disables halting on errors
	 */
	public ASaxParserHandler(final boolean bEnableHaltOnParsingError) {
		super();		
		reset();
		setSaxHandlerLocator( new LocatorImpl() );
		
		bError_HaltParsingOnError = bEnableHaltOnParsingError;
	}
	
	/**
	 * Tells if a parsing error will cause an abortion of parsing.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bError_HaltParsingOnError
	 * 
	 * @return true if a parsing error will cause an parsing abortion
	 */
	final protected boolean isHaltOnParsingErrorSet() {
		return bError_HaltParsingOnError;
	}
	
	/**
	 * Get a reference to the current Locator.
	 * 
	 * @see org.xml.sax.helpers.LocatorImpl
	 * @see org.xml.sax.Locator
	 * 
	 * @return eference to the current Locator
	 */
	final protected Locator getSaxHandlerLocator() {
		return this.refLocator;
	}
	
	/**
	 * Sets the reference to the current Locator
	 * 
	 * @see org.xml.sax.helpers.LocatorImpl
	 * @see org.xml.sax.Locator
	 * 
	 * @param setLocator
	 */
	final protected void setSaxHandlerLocator(LocatorImpl setLocator) {
		assert setLocator!=null : "setSaxHandlerLocator() Error due to null-pointer";
		
		System.out.println("DUMDIDUM");
		
		this.refLocator = setLocator;
		setDocumentLocator( refLocator );
	}
	
	/**
	 * Details on current location during parsing.
	 * 
	 * @return current line and column number 
	 */
	final protected String detailsOnLocationInXML() {
		return " line=" +
			refLocator.getLineNumber() + ":" +
			refLocator.getColumnNumber();
	}
	
	/**
	 * Append an error message and set the "error-has-occurred"-flag true.
	 * In comparision cerberus.net.dwt.swing.DParseBaseSaxHandler#appandInfoMsg(String) sets an info message
	 * without setting the "error-has-occurred"-flag true.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#appandInfoMsg(String)
	 * 
	 * @param errorMessage new error message
	 */
	final protected void appandErrorMsg( final String errorMessage ) {
		
		if ( sErrorMessage.length() > 1 ) {
			sErrorMessage += "\n";
		}
		sErrorMessage += errorMessage + detailsOnLocationInXML();
		
		if ( bError_HaltParsingOnError ) {
			try {
				this.fatalError( 
						new SAXParseException(
								"ParseException due to "+errorMessage,
								refLocator ));
			}
			catch (SAXException s_e) {
				throw new GeneViewRuntimeException(s_e.toString(),
						GeneViewRuntimeExceptionType.SAXPARSER );
			}
		}
		if ( ! bErrorWhileParsing )
			bErrorWhileParsing = true;
	}
	
	/**
	 * Appands an new debug information, which can be read after parsing by calling
	 * cerberus.net.dwt.swing.DParseBaseSaxHandler#
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 * @param infoMessage
	 */
	final protected void appandInfoMsg( final String infoMessage ) {
		
		if ( sInfoMessage.length() > 1 ) {
			sInfoMessage += "\n";
		}
		sInfoMessage += infoMessage + detailsOnLocationInXML();
	}
	
	/**
	 * Returns the error message. An error message also always sets
	 * cerberus.net.dwt.swing.DParseBaseSaxHandler#bErrorWhileParsing true,
	 * which can be tested via the method 
	 * cerberus.net.dwt.swing.DParseBaseSaxHandler#hasErrorWhileParsing() .
	 * 
	 * @return text of error message. If no error occurred this String is empty.
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#hasErrorWhileParsing()
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#getInfoMessage()
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#bErrorWhileParsing
	 */
	public final String getErrorMessage() {
		return sErrorMessage;
	}
	
	/**
	 * Returns the info message.
	 * 
	 * @return text of info message. 
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#hasErrorWhileParsing()
	 */
	public final String getInfoMessage() {
		return sInfoMessage;
	}
	
	/**
	 * Test if parsing was successful.
	 * 
	 * To get error message call  cerberus.net.dwt.swing.DButtonSaxHandler#getErrorMessage()
	 * 
	 * @return TRUE if an error occurred on parsing. 
	 * 
	 * @see cerberus.net.dwt.swing.parser.ASaxParserHandler#getErrorMessage()
	 */
	public final boolean hasErrorWhileParsing() {
		return bErrorWhileParsing;
	}
	
	/**
	 * Tries to convert a String to an int.
	 * Creates a suitable error message in case of an error.
	 * 
	 * @param parsingString String to be converted to int
	 * @return value of String
	 */
	final protected int parseStringToInt( final String parsingString ) {
		try {
			return Integer.valueOf( parsingString );
		}
		catch (NumberFormatException ne) {
			appandErrorMsg("ERROR in conversion of [" +
					parsingString + "] to integer value" );
			return 0;
		}
	}
	
	/**
	 * Tries to convert a String to an boolean.
	 * Creates a suitable error message in case of an error.
	 * 
	 * @param parsingString String to be converted to int
	 * @return boolean value of String
	 */
	final protected boolean parseStringToBool( final String parsingString ) {
		try {
			return Boolean.valueOf( parsingString );
		}
		catch (NumberFormatException ne) {
			appandErrorMsg("ERROR in conversion of [" +
					parsingString + "] to boolean value" );
			return false;
		}
	}
	
	
	/**
	 * Important: all derived classes must call super.reset() inside their reset() call
	 * to not cause side effects!
	 * 
	 * @see cerberus.parser.xml.sax.ISaxParserHandler#reset()
	 */
	public void reset() {
		sErrorMessage = "";
		sInfoMessage = "";
		bErrorWhileParsing = false;	
	}
	
	/**
	 * Sets the reference to the calling memento object.
	 * Used to tigger a callback event
	 * 
	 * @see 
	 * 
	 * @param setRefParent reference to the parent obejct or the object, that sould be triggert in case of a callback action
	 */
	public void setParentMementoCaller( IMementoCallbackXML setRefParent ) {
		refParentMementoCaller = setRefParent;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.DParseSaxHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public abstract void startElement(String uri, 
			String localName, 
			String qName, 
			Attributes attributes);
	
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.swing.DParseSaxHandler#endElement(Stringt, Stringt, Stringt)
	 */
	public abstract void endElement(String uri, String localName, String qName );

	/**
	 * Defines the type of widget or component.
	 * 
	 * @return type of widget or component
	 */
	public abstract String getXML_ViewCanvas_Type();
	
}
