package org.caleydo.core.util.clusterer;

public class EuclideanDistance
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

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
