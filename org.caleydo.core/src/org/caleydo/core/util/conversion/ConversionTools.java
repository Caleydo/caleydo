package org.caleydo.core.util.conversion;

import java.util.StringTokenizer;
import javax.management.InvalidAttributeValueException;

/**
 * Class that provides static methods to convert between different things
 * 
 * @author Alexander Lex
 */
public class ConversionTools {

	/**
	 * The string is expected to contain colors in the format "255,255,255" with values ranging from 0 to 255.
	 * The returned array is of type float with values between 0 and 1.
	 * 
	 * @param sColor
	 *            the string containing the color with values between 0 and 255
	 * @return a float array containing the colors with values between 0 and 1
	 */
	public static float[] getFloatColorFromString(String sColor) {
		float[] fArColor = new float[3];
		if (sColor.isEmpty()) {
			fArColor[0] = 0;
			fArColor[1] = 0;
			fArColor[2] = 0;
		}
		else {
			StringTokenizer tokenizer = new StringTokenizer(sColor, ",", false);
			int iInnerCount = 0;
			while (tokenizer.hasMoreTokens()) {
				try {
					String token = tokenizer.nextToken();
					int iTemp = Integer.parseInt(token);
					fArColor[iInnerCount] = (float) iTemp / 255;
				}
				catch (Exception e) {

				}
				iInnerCount++;
			}
		}
		return fArColor;
	}

	/**
	 * The string is expected to contain colors in the format "255,255,255" with values ranging from 0 to 255.
	 * The returned array is of type float with values between 0 and 1.
	 * 
	 * @param sColor
	 *            the string containing the color with values between 0 and 255
	 * @return a float array containing the colors with values between 0 and 1
	 */
	public static int[] getIntColorFromString(String color) {

		int[] iArColor = new int[3];
		if (color.isEmpty()) {
			iArColor[0] = 0;
			iArColor[1] = 0;
			iArColor[2] = 0;
		}
		else {
			StringTokenizer tokenizer = new StringTokenizer(color, ",", false);
			int iInnerCount = 0;
			while (tokenizer.hasMoreTokens()) {
				try {
					String token = tokenizer.nextToken();
					iArColor[iInnerCount] = Integer.parseInt(token);
				}
				catch (Exception e) {

				}
				iInnerCount++;
			}
		}

		return iArColor;
	}

	/**
	 * Does the actual normalization between 0 and 1 values that are NaN in the input are kept to be NaN
	 * 
	 * @param inputData
	 *            the input data
	 * @param min
	 *            the minimum considered in the normalization
	 * @param max
	 *            the maximum considered in the normalization
	 * @return
	 * @throws InvalidAttributeValueException
	 *             when fMin is >= fMax
	 */
	public static float[] normalize(float[] inputData, final float min, final float max) {
		if (min > max)
			throw new IllegalArgumentException("Minimum (" + min + ") was bigger as maximum (" + max + ")");

		float[] targetData = new float[inputData.length];
		if (inputData.length > 1) {

			for (int iCount = 0; iCount < inputData.length; iCount++) {
				if (Float.isNaN(inputData[iCount])) {
					targetData[iCount] = Float.NaN;
				}

				targetData[iCount] = (inputData[iCount] - min) / (max - min);
				if (targetData[iCount] > 1) {
					targetData[iCount] = 1;
				}
				else if (targetData[iCount] < 0) {
					targetData[iCount] = 0;
				}
			}
		}
		return targetData;
	}

	/**
	 * Does the actual normalization between 0 and 1 values that are NaN in the input are kept to be NaN
	 * 
	 * @param inputData
	 *            the input data
	 * @param min
	 *            the minimum considered in the normalization
	 * @param max
	 *            the maximum considered in the normalization
	 * @param calculateAbsolute
	 *            if true the sign (+/-) is ignored
	 * @return
	 * @throws InvalidAttributeValueException
	 *             when fMin is >= fMax
	 */
	public static double[] normalize(double[] inputData, final double min, final double max,
		boolean calculateAbsolute) {

		if (min > max)
			throw new IllegalArgumentException("Minimum (" + min + ") was bigger as maximum (" + max + ")");

		double[] targetData = new double[inputData.length];
		if (inputData.length > 1) {

			for (int iCount = 0; iCount < inputData.length; iCount++) {
				if (Double.isNaN(inputData[iCount])) {
					targetData[iCount] = Float.NaN;
				}

				if (calculateAbsolute)
					targetData[iCount] = (Math.abs(inputData[iCount]) - min) / (max - min);
				else
					targetData[iCount] = (inputData[iCount] - min) / (max - min);

				if (targetData[iCount] > 1) {
					targetData[iCount] = 1;
				}
				else if (targetData[iCount] < 0) {
					targetData[iCount] = 0;
				}
			}
		}
		return targetData;
	}

	/**
	 * Does the actual normalization between 0 and 1 values that are NaN in the input are kept to be NaN
	 * 
	 * @param inputData
	 *            the input data
	 * @param min
	 *            the minimum considered in the normalization
	 * @param max
	 *            the maximum considered in the normalization
	 * @return
	 * @throws InvalidAttributeValueException
	 *             when fMin is >= fMax
	 */
	public static double[] normalize(double[] inputData, final double min, final double max) {

		return normalize(inputData, min, max, false);
	}

	/**
	 * Convert a String sInput into an array of integers. The Int array size is asigns dynamically depending
	 * on the size of provided integer values inside the string sInput. If number of integer values in sInput
	 * is smaller than iDimension the remaining values are "0". Also if a non-integer value is found it is
	 * replaced by "0".
	 * 
	 * @param sInput
	 *            input to be converted into an array of int[]
	 * @param sDelimiter
	 *            delimiter used while parsing String
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
