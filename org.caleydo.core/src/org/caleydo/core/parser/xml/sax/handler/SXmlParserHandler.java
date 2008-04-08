/**
 * 
 */
package org.caleydo.core.parser.xml.sax.handler;

import org.xml.sax.Attributes;

import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public final class SXmlParserHandler
{

	/**
	 * 
	 */
	private SXmlParserHandler()
	{
		
	}
	
	/**
	 * Read (int) key from Attributes, if key is not present a defautl value 
	 * is returned.
	 * 
	 * @param attrs Attributes from SAX-parser
	 * @param key key to search for
	 * @return integer of -1 in case key was not present
	 */
	public static final int assignIntValueIfValid_orReturnNegative( 
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
	public static final boolean assignBooleanValueIfValid( 
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
	public static final String assignStringValue( final Attributes attrs,
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
	public static final int assignIntValueIfValid( final Attributes attrs,
			final String key,
			final int iDefaultValue) {		
		String sBuffer = attrs.getValue( key );
		if ( sBuffer != null  ) {
			return Integer.valueOf(  sBuffer );
		}
		return iDefaultValue;
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
	public static final int assignIntValueIfValid( final Attributes attrs,
			final String key,
			final int iDefaultValue,
			IParameterHandler parameterHandler ) {		
		String sBuffer = attrs.getValue( key );
		
		if ( sBuffer != null  ) {
			parameterHandler.setValueAndType( key, 
					sBuffer, 
					IParameterHandler.ParameterHandlerType.STRING);
			
			return Integer.valueOf(  sBuffer );
		}
		
		return iDefaultValue;
	}
	
	

}
