package org.caleydo.core.util.clusterer;

import java.util.Arrays;

public class ClusterHelper {
	
	/**
	 * Calculates the mean for a given vector (float array)
	 * 
	 * @param vector
	 * @return mean
	 */
	public static float mean(float[] vector) {
		float mean = 0;

		for (int i = 0; i < vector.length; i++)
			mean += vector[i];

		return mean / vector.length;
	}

	/**
	 * Calculates the median for a given vector (float array)
	 * 
	 * @param vector
	 * @return median
	 */
	public static float median(float[] vector) {
		float median = 0;
		float[] temp = new float[vector.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = vector[i];

		Arrays.sort(temp);

		if ((temp.length % 2) == 0)
			median =
				(temp[(int) Math.floor(temp.length / 2)] + temp[(int) Math.floor((temp.length + 1) / 2)]) / 2;
		else
			median = temp[(int) Math.floor((temp.length + 1) / 2)];

		return median;
	}

	/**
	 * Calculates the minimum for a given vector (float array)
	 * 
	 * @param vector
	 * @return double minimum
	 */
	public static float minimum(float[] dArray) {
		float[] temp = new float[dArray.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = dArray[i];

		Arrays.sort(temp);

		return temp[0];
	}
}
