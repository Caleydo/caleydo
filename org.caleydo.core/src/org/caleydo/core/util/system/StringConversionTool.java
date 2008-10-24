package org.caleydo.core.util.system;

import java.util.StringTokenizer;

/**
 * Conversion of String to int and boolean using default values, in case
 * conversion fails.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 * @deprecated Use java standard way instead
 */
public final class StringConversionTool
{
	/**
	 * Convert String to int, debug version.
	 * 
	 * @param sInput String to convert
	 * @param iDefault default value
	 * @return converted int or default value, if (String) could not be
	 *         converted to (int).
	 */
	public static final int convertStringToInt(final String sInput, final int iDefault)
	{
		try
		{
			return Integer.valueOf(sInput).intValue();
		}
		catch (NumberFormatException nfe)
		{
			return iDefault;
		}
	}

	/**
	 * Convert String to Float.
	 * 
	 * @param sInput String to convert
	 * @param fDefault default value
	 * @return converted int or default value, if (String) could not be
	 *         converted to (float).
	 */
	public static final float convertStringToFloat(final String sInput, final float fDefault)
	{
		try
		{
			return Float.valueOf(sInput).floatValue();
		}
		catch (NumberFormatException nfe)
		{
			return fDefault;
		}
	}

	/**
	 * Convert String to Double.
	 * 
	 * @param sInput String to convert
	 * @param dDefault default value
	 * @return converted int or default value, if (String) could not be
	 *         converted to (doulbe).
	 */
	public static final double convertStringToDouble(final String sInput, final double dDefault)
	{
		try
		{
			return Double.valueOf(sInput).doubleValue();
		}
		catch (NumberFormatException nfe)
		{
			return dDefault;
		}
	}

	/**
	 * Convert String to Long.
	 * 
	 * @param sInput String to convert
	 * @param lDefault default value
	 * @return converted int or default value, if (String) could not be
	 *         converted to (long).
	 */
	public static final long convertStringToLong(final String sInput, final long lDefault)
	{
		try
		{
			return Long.valueOf(sInput).longValue();
		}
		catch (NumberFormatException nfe)
		{
			return lDefault;
		}
	}

	/**
	 * Convert String to String checking is (String) is larger than "" and is
	 * not null. Otherwise the sDefault is returned.
	 * 
	 * @param sInput String to convert
	 * @param sDefault default value
	 * @return sInput if it is not "" and nut null, else sDefault is returned
	 */
	public static final String convertStringToString(final String sInput, final String sDefault)
	{
		if ((sInput != null) && (sInput.length() > 0))
		{
			return sInput;
		}

		return sDefault;
	}

	/**
	 * Convert String to boolean.
	 * 
	 * @param sInput String to convert
	 * @param bDefault default value
	 * @return converted boolean or default value, if (String) could not be
	 *         converted to (boolean).
	 */
	public static final boolean convertStringToBoolean(final String sInput,
			final boolean bDefault)
	{
		try
		{
			return Boolean.valueOf(sInput).booleanValue();
		}
		catch (NumberFormatException nfe)
		{
			return bDefault;
		}
	}

	/**
	 * Convert a String sInput into an array of integers. The Int array size is
	 * asigns dynamically depending on the size of provided integer values
	 * inside the string sInput. If number of integer values in sInput is
	 * smaller than iDimension the remaining values are "0". Also if a
	 * non-integer value is found it is replaced by "0".
	 * 
	 * @param sInput input to be converted into an array of int[]
	 * @param sDelimiter delimiter used while parsing String
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArrayVariableLength(final String sInput,
			final String sDelimiter)
	{
		StringTokenizer tokenize = new StringTokenizer(sInput, sDelimiter);
		int[] resultIntArray = new int[tokenize.countTokens()];

		for (int i = 0; tokenize.hasMoreTokens(); i++)
		{
			resultIntArray[i] = convertStringToInt(tokenize.nextToken(), 0);
		}

		return resultIntArray;
	}
}
