package org.caleydo.core.util.conversion;

import java.util.StringTokenizer;

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
	 * @param sColor the string containing the color with values between 0 and 255
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
	 * @param sColor the string containing the color with values between 0 and 255
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

}
