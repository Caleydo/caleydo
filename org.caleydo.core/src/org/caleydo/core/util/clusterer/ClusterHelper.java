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

	/**
	 * Calculates the pearson correlation for given vectors (float arrays)
	 * 
	 * @param vector1
	 * @param vector2
	 * @return float pearson correlation
	 */
	public static float pearsonCorrelation(float[] vector1, float[] vector2) {
		float correlation = 0;
		float sum_sq_x = 0;
		float sum_sq_y = 0;
		float sum_coproduct = 0;
		float mean_x = mean(vector1);
		float mean_y = mean(vector2);

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {
			float delta_x = vector1[i] - mean_x;
			float delta_y = vector2[i] - mean_y;
			sum_sq_x += delta_x * delta_x;
			sum_sq_y += delta_y * delta_y;
			sum_coproduct += delta_x * delta_y;
		}
		float pop_sd_x = (float) Math.sqrt(sum_sq_x / vector1.length);
		float pop_sd_y = (float) Math.sqrt(sum_sq_y / vector1.length);
		float cov_x_y = sum_coproduct / vector1.length;
		correlation = cov_x_y / (pop_sd_x * pop_sd_y);

		return correlation;
	}

	/**
	 * Calculates the euclidean distance for given vectors (float arrays)
	 * 
	 * @param vector1
	 * @param vector2
	 * @return float euclidean distance
	 */
	public static float euclideanDistance(float[] vector1, float[] vector2) {
		float distance = 0;
		float sum = 0;

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {
			sum = (float) (sum + Math.pow(vector1[i] - vector2[i], 2));
		}

		distance = (float) Math.sqrt(sum);

		return distance;
	}
}
