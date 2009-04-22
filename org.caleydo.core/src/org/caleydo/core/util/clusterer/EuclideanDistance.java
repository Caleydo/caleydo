package org.caleydo.core.util.clusterer;

public class EuclideanDistance
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

		float distance = 0;

		float sum = 0;

		float temp_v1 = 0;
		float temp_v2 = 0;

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {
			if (Float.isNaN(vector1[i]))
				temp_v1 = 0;
			else
				temp_v1 = vector1[i];

			if (Float.isNaN(vector2[i]))
				temp_v2 = 0;
			else
				temp_v2 = vector2[i];

			sum = (float) (sum + Math.pow(temp_v1 - temp_v2, 2));
		}

		distance = (float) Math.sqrt(sum);

		return distance;
	}
}
