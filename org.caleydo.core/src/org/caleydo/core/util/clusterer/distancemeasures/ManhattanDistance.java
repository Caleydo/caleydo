package org.caleydo.core.util.clusterer.distancemeasures;

/**
 * Manhattan distance measure, implements {@link IDistanceMeasure}.
 * 
 * @author Bernhard Schlegl
 */
public class ManhattanDistance
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

		float distance = 0;
		float temp_diff = 0;

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {

			if (Float.isNaN(vector1[i]) || Float.isNaN(vector2[i]))
				temp_diff = 0;
			else
				temp_diff = Math.abs(vector1[i] - vector2[i]);

			distance += temp_diff;
		}

		return distance;
	}
}
