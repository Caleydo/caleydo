package org.caleydo.core.util.clusterer;

public class PearsonCorrelation
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

		float correlation = 0;
		float sum_sq_x = 0;
		float sum_sq_y = 0;
		float sum_coproduct = 0;
		float mean_x = ClusterHelper.arithmeticMean(vector1);
		float mean_y = ClusterHelper.arithmeticMean(vector2);

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {

			float delta_x = 0, delta_y = 0;

			if (Float.isNaN(vector1[i]) == false)
				delta_x = vector1[i] - mean_x;
			if (Float.isNaN(vector2[i]) == false)
				delta_y = vector2[i] - mean_y;

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
}
