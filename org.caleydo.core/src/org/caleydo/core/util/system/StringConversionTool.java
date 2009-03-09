package org.caleydo.core.util.system;

import java.util.StringTokenizer;

/**
 * Conversion helper functions.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public final class StringConversionTool {
	/**
	 * Convert a String sInput into an array of integers. The Int array size is asigns dynamically depending on
	 * the size of provided integer values inside the string sInput. If number of integer values in sInput is
	 * smaller than iDimension the remaining values are "0". Also if a non-integer value is found it is replaced
	 * by "0".
	 * 
	 * @param sInput
	 *          input to be converted into an array of int[]
	 * @param sDelimiter
	 *          delimiter used while parsing String
	 * @return a new array of int[]
	 */
	public static final int[] convertStringToIntArray(final String sInput, final String sDelimiter) {
		StringTokenizer tokenize = new StringTokenizer(sInput, sDelimiter);
		int[] resultIntArray = new int[tokenize.countTokens()];

		for (int i = 0; tokenize.hasMoreTokens(); i++) {
			resultIntArray[i] = Integer.valueOf(tokenize.nextToken()).intValue();
		}

		return resultIntArray;
	}
}
