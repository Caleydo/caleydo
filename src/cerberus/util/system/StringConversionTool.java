/**
 * 
 */
package cerberus.util.system;

import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Conversion of String to int and boolean using default values, 
 * in case conversion failes.
 * 
 * @author kalkusch
 */
public final class StringConversionTool
{

	/**
	 * Hidden constructor
	 */
	private StringConversionTool()
	{
		
	}

	/**
	 * Convert String to int.
	 * 
	 * @param sInput String to convert
	 * @param iDefault default value
	 * @return converted int or default value, if (String) could not be converted to (int).
	 */
	public static final int convertStringToInt( 
			final String sInput, 
			final int iDefault ) {
		try {
			return Integer.valueOf(sInput);
		}
		catch ( NumberFormatException nfe ) 
		{			
			return iDefault;
		}
	}
	
	/**
	 * Convert String to String checking is (String) is larger than "" and is not null.
	 * Otherwide the sDefault is returned.
	 * 
	 * @param sInput String to convert
	 * @param sDefault default value
	 * @return sInput if it is not "" and nut null, else sDefault is returned
	 */
	public static final String convertStringToString( 
			final String sInput, 
			final String sDefault ) {
		if (( sInput != null )&&( sInput.length() > 0 )) {
			return sInput;
		}
		
		return sDefault;
	}
	
	/**
	 * Convert String to boolean.
	 * 
	 * @param sInput String to convert
	 * @param bDefault default value
	 * @return converted boolean or default value, if (String) could not be converted to (boolean).
	 */
	public static final boolean convertStringToInt( 
			final String sInput, 
			final boolean bDefault ) {
		try {
			return Boolean.valueOf(sInput);
		}
		catch ( NumberFormatException nfe ) 
		{			
			return bDefault;
		}
	}
	
}
