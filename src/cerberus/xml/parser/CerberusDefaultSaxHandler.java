package cerberus.xml.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import cerberus.manager.GeneralManager;
import cerberus.manager.MenuManager;

public abstract class CerberusDefaultSaxHandler extends DefaultHandler {

	protected final String sArgumentBegin = "=\""; 
	protected final String sArgumentEnd   = "\" "; 
	
	protected final MenuManager    refMenuMenager;
	protected final GeneralManager refGeneralManager;
		
	protected String sErrorMessage = "";
	
	
	protected CerberusDefaultSaxHandler( final GeneralManager setGeneralManager ) {
		refGeneralManager = setGeneralManager;
		refMenuMenager = setGeneralManager.getSingelton().getMenuManager();
		
		assert refMenuMenager != null : "MenuManager was not instanciated in Singelton!";
	}
	
	protected final int assignIntValueIfValid_orReturnNegative( final Attributes attrs,
			final String key ) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Integer.valueOf(  sBuffer );
		}
		return -1;
	}
	
	protected final boolean assignBooleanValueIfValid( final Attributes attrs,
			final String key,
			final boolean bDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Boolean.valueOf(  sBuffer );
		}
		return bDefaultValue;
	}
	
	/**
	 * Get String from a Attributes attrs. If key does not exist sDefaultValue is returned.
	 * 
	 * @param attrs SAX attributes
	 * @param key key to search for
	 * @param sDefaultValue default key
	 * @return
	 */
	protected final String assignStringValue( final Attributes attrs,
			final String key,
			final String sDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		
		if ( sBuffer == null  ) {
			return sDefaultValue;
		}
		
		return sBuffer;
	}
	
	protected final int assignIntValueIfValid( final Attributes attrs,
			final String key,
			final int iDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Integer.valueOf(  sBuffer );
		}
		return iDefaultValue;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.jogl.CerberusSaxHandler#getErrorMessage()
	 */
	public String getErrorMessage() {
		return sErrorMessage;
	}
	
	public abstract String createXMLcloseingTag(final Object frame, final String sIndent);

	public abstract String createXML(final Object frame, final String sIndent);


}