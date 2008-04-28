/**
 * 
 */
package org.caleydo.core.util.system;

import java.util.StringTokenizer;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;

//import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Conversion of String to int and boolean using default values, 
 * in case conversion failes.
 * 
 * @author Michael Kalkusch
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
	 * Convert String to int, debug version.
	 * 
	 * @param sInput String to convert
	 * @param iDefault default value
	 * @param generalManager reference to Logger
	 * @return converted int or default value, if (String) could not be converted to (int).
	 */
	public static final int convertStringToInt( 
			final ILoggerManager generalManager,
			final String sInput, 
			final int iDefault ) {
		try {
			return Integer.valueOf(sInput).intValue();
		}
		catch ( NumberFormatException nfe ) 
		{			
			generalManager.logMsg( "convertStringToInt( " +
					sInput + ") invalid String, use default=[" +
					iDefault + "]",
					LoggerType.VERBOSE );
			return iDefault; 
		}
	}
	
	/**
	 * Convert String to Float.
	 * 
	 * @param sInput String to convert
	 * @param fDefault default value
	 * @return converted int or default value, if (String) could not be converted to (float).
	 */
	public static final float convertStringToFloat( 
			final String sInput, 
			final float fDefault ) {
		try {
			return Float.valueOf(sInput).floatValue();
		}
		catch ( NumberFormatException nfe ) 
		{			
			return fDefault;
		}
	}
	
	/**
	 * Convert String to Double.
	 * 
	 * @param sInput String to convert
	 * @param dDefault default value
	 * @return converted int or default value, if (String) could not be converted to (doulbe).
	 */
	public static final double convertStringToDouble( 
			final String sInput, 
			final double dDefault ) {
		try {
			return Double.valueOf(sInput).doubleValue();
		}
		catch ( NumberFormatException nfe ) 
		{			
			return dDefault;
		}
	}
	
	/**
	 * Convert String to Long.
	 * 
	 * @param sInput String to convert
	 * @param lDefault default value
	 * @return converted int or default value, if (String) could not be converted to (long).
	 */
	public static final long convertStringToLong( 
			final String sInput, 
			final long lDefault ) {
		try {
			return Long.valueOf(sInput).longValue();
		}
		catch ( NumberFormatException nfe ) 
		{			
			return lDefault;
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
	public static final boolean convertStringToBoolean( 
			final String sInput, 
			final boolean bDefault ) {
		try {
			return Boolean.valueOf(sInput).booleanValue();
		}
		catch ( NumberFormatException nfe ) 
		{			
			return bDefault;
		}
	}
	
	/**
	 * Convert a String sInput into an array of intergers using iDimension assize for the result array.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of the array of int[]
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArray(
			final String sInput, 
			final int iDimension ) {
		
		assert iDimension > 1 : "dimension must be at least 2!";
		
		int [] resultIntArray = new int[iDimension];
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
			
		for ( int i=0; tokenize.hasMoreTokens(); i++ ) 
		{
			if ( i >= iDimension )
			{
				break;
			}
			
			resultIntArray[i] = convertStringToInt( 
				tokenize.nextToken(), 0 );					
		}
		
		return resultIntArray;		
	}
	
	/**
	 * Convert a String sInput into an array of intergers (debug version) using iDimension assize for the result array.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of the array of int[]
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArray(
			final ILoggerManager generalManager,
			final String sInput, 
			final int iDimension ) {
		
		assert iDimension > 1 : "dimension must be at least 2!";
		assert sInput != null : "can not handle String null-pointer!";
		
		int [] resultIntArray = new int[iDimension];
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
		
		int i=0;
		for (  ; tokenize.hasMoreTokens(); i++ ) 
		{
			if ( i >= iDimension )
			{
				generalManager.logMsg( "parsing [" + sInput + "] should contain [" + 
						iDimension + "] values. Skip remaining values!",
						LoggerType.VERBOSE );
				break;
			}
			
			resultIntArray[i] = convertStringToInt( 
				tokenize.nextToken(), 0 );					
		}
		
		if ( i < iDimension ) {
			generalManager.logMsg( "parsing [" + sInput + "] should contain [" + 
					iDimension + "] values. use valeu '0' for remaining values!",
					LoggerType.VERBOSE );
		}
		
		return resultIntArray;		
	}
	
	
	/**
	 * Convert a String sInput into an array of intergers using iDimension assize for the result array.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of the array of int[]
	 * @param iDefaultArray use this array as default array in case sInput conrains no  or wrong values.
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArray(
			final String sInput, 
			final int iDimension,
			final int[] iDefaultArray ) {
		
		assert iDimension > 1 : "dimension must be at least 2!";
		
		int [] resultIntArray = new int[iDimension];
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
			
		for ( int i=0; tokenize.hasMoreTokens(); i++ ) 
		{
			if ( i >= iDimension )
			{
				break;
			}
			
			resultIntArray[i] = convertStringToInt( 
				tokenize.nextToken(), iDefaultArray[i] );					
		}
		
		return resultIntArray;		
	}
	
	/**
	 * Convert a String sInput into an array of intergers (debug version) using iDimension assize for the result array.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * @param generalManager reference to Logger
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of the array of int[]
	 * @param iDefaultArray use this array as default array in case sInput conrains no  or wrong values.
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArray(
			final ILoggerManager generalManager,
			final String sInput, 
			final int iDimension,
			final int[] iDefaultArray ) {
		
		assert iDimension > 1 : "dimension must be at least 2!";
		
		int [] resultIntArray = new int[iDimension];
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
			
		for ( int i=0; tokenize.hasMoreTokens(); i++ ) 
		{
			if ( i >= iDimension )
			{
				generalManager.logMsg("convertStringToIntArray() Skip remaining tokens in array [" +
						sInput + 
						"] result=[" +
						resultIntArray.toString() + "]",
						LoggerType.STATUS );
				break;
			}
			
			resultIntArray[i] = convertStringToInt( 
					generalManager,
					tokenize.nextToken(), 
					iDefaultArray[i] );					
		}
		
		return resultIntArray;		
	}
	
	/**
	 * Convert a String sInput into an array of intergers (debug version).
	 * The Int array size is asigns dynamically depending on the size of provided integer values inside the string sInput.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * @param generalManager reference to Logger
	 * @param sInput input to be converted into an array of int[]
	 * @param sDelimiter delimiter used while parsing String
	 * 
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArrayVariableLength(
			final ILoggerManager generalManager,
			final String sInput,
			final String sDelimiter ) { 
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				sDelimiter );
			
		int [] resultIntArray = new int[tokenize.countTokens()];
		
		if ( resultIntArray.length < 1 ) 
		{
			generalManager.logMsg("Can not read int[] array with length 0!",
					LoggerType.MINOR_ERROR ); 
		}
		
		for ( int i=0; tokenize.hasMoreTokens(); i++ ) 
		{
			resultIntArray[i] = convertStringToInt( 
					tokenize.nextToken(),
					0 );					
		}
		
		return resultIntArray;		
	}
	
	/**
	 * Convert a String sInput into an array of intergers.
	 * The Int array size is asigns dynamically depending on the size of provided integer values inside the string sInput.
	 * If number of integer values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-integer value is found it is replaced by "0".
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param sDelimiter delimiter used while parsing String
	 * 
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArrayVariableLength(
			final String sInput,
			final String sDelimiter ) { 
		
		StringTokenizer tokenize = new StringTokenizer( sInput,
				sDelimiter );
			
		int [] resultIntArray = new int[tokenize.countTokens()];
		
//		if ( resultIntArray.length < 1 ) 
//		{
//			assert false : "Can not read int[] array with length 0!"; 
//		}
		
		for ( int i=0; tokenize.hasMoreTokens(); i++ ) 
		{
			resultIntArray[i] = convertStringToInt( 
					tokenize.nextToken(),
					0 );					
		}
		
		return resultIntArray;		
	}
	
	/**
	 * Convert a String sInput into an array of floats.
	 * The Float array size is assigns by iDimension.
	 * If number of float values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-float value is found it is replaced by "0".
	 * 
	 * @see org.caleydo.core.util.system.StringConversionTool#convertStringToFloatArrayVariableLength(String)
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of flaot array
	 * 
	 * @return a new array of int[]
	 */
	public static final float[] convertStringToFloatArray(
			final String sInput,
			final int iDimension ) { 
		
		StringTokenizer tokenizer = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
		
		float [] resultArray = new float [iDimension];
		
		int i=0;
		for ( ; tokenizer.hasMoreTokens(); i++ ) 
		{
			if ( i >= iDimension )
			{
				assert false : "to many float values in String! Skip remaining!";
				break;
			}
			
			resultArray[i] = convertStringToFloat( 
					tokenizer.nextToken(),
					0.0f );
		} //for
		
		return resultArray;
	}
	
	/**
	 * Convert a String sInput into an array of floats.
	 * The Float array size is asigned dynamically depending on the size of provided float values inside the string sInput.
	 * If number of float values in sInput is smaller than iDimension the remaining values are "0".
	 * Also if a non-float value is found it is replaced by "0".
	 * 
	 * @see org.caleydo.core.util.system.StringConversionTool#convertStringToFloatArray(String, int)
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param iDimension size of flaot array
	 * 
	 * @return a new array of int[]
	 */
	public static final float[] convertStringToFloatArrayVariableLength(
			final String sInput ) { 
		StringTokenizer tokenizer = new StringTokenizer( sInput,
				IGeneralManager.sDelimiter_Parser_DataItems );
		
		int iDimension = tokenizer.countTokens();
		
		float [] resultArray = new float [iDimension];
		
		int i=0;
		for ( ; tokenizer.hasMoreTokens(); i++ ) 
		{
			resultArray[i] = convertStringToFloat( 
					tokenizer.nextToken(),
					0.0f );
		} //for
		
		return resultArray;
	}
	
}
