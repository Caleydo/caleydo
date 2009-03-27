package org.caleydo.core.util.clusterer;

public interface IDistanceMeasure {

	/**
	 * Calculates the distance measure between to given vectors and returns the result.
	 * 
	 * @param vector1
	 *            first vector
	 * @param vector2
	 *            second vector
	 * @return result of distance measure calculation
	 */
	public float getMeasure(float[] vector1, float[] vector2);

}
