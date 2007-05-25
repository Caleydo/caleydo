package cerberus.xml.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;

public abstract class ACerberusDefaultSaxHandler extends DefaultHandler {

	protected final String sArgumentBegin = "=\""; 
	protected final String sArgumentEnd   = "\" "; 
	
	protected final IMenuManager    refMenuMenager;
	protected final IGeneralManager refGeneralManager;
		
	protected String sErrorMessage = "";
	
	
	protected ACerberusDefaultSaxHandler( 
			final IGeneralManager setGeneralManager ) {
		refGeneralManager = setGeneralManager;
		refMenuMenager = setGeneralManager.getSingelton().getMenuManager();
		
		assert refMenuMenager != null : "MenuManager was not instanciated in ISingelton!";
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.jogl.CerberusSaxHandler#getErrorMessage()
	 */
	public String getErrorMessage() {
		return sErrorMessage;
	}
	
	public abstract String createXMLcloseingTag(
			final Object frame, 
			final String sIndent);

	public abstract String createXML(
			final Object frame, 
			final String sIndent);


	
	/**
	 * Read (int) key from Attributes, if key is not present a defautl value 
	 * is returned.
	 * 
	 * @param attrs Attributes from SAX-parser
	 * @param key key to search for
	 * @return integer of -1 in case key was not present
	 */
	protected static final int assignIntValueIfValid_orReturnNegative( 
			final Attributes attrs,
			final String key ) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Integer.valueOf(  sBuffer );
		}
		return -1;
	}
	
	/**
	 * Read (boolean) key from Attributes, if key is not present 
	 * a default value is returned.
	 * 
	 * @param attrs Attributes from SAX-parser
	 * @param key key to search for
	 * @return integer of -1 in case key was not present
	 */
	protected static final boolean assignBooleanValueIfValid( 
			final Attributes attrs,
			final String key,
			final boolean bDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Boolean.valueOf(  sBuffer );
		}
		return bDefaultValue;
	}
	
	/**
	 * Get String from a Attributes attrs. If key does not exist sDefaultValue 
	 * is returned.
	 * 
	 * @param attrs SAX attributes
	 * @param key key to search for
	 * @param sDefaultValue default value
	 * @return data assigned to the key in Attributes or default value
	 */
	protected static final String assignStringValue( final Attributes attrs,
			final String key,
			final String sDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		
		if ( sBuffer == null  ) {
			return sDefaultValue;
		}
		
		return sBuffer;
	}
	
	/**
	 * Read (int) key from Attributes, if key is not present a defautl value 
	 * is returned.
	 * 
	 * @param attrs Attributes from SAX-parser
	 * @param key key to search for
	 * @param iDefaultValue default value
	 * @return integer of key of default value
	 */
	protected static final int assignIntValueIfValid( final Attributes attrs,
			final String key,
			final int iDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Integer.valueOf(  sBuffer );
		}
		return iDefaultValue;
	}
	

}