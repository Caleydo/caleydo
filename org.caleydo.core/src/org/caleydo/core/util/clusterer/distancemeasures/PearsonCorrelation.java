/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer.distancemeasures;



/**
 * Pearson correlation measure, implements {@link IDistanceMeasure}.
 *
 * @author Bernhard Schlegl
 */
public class PearsonCorrelation
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

		float correlation = 0;
		float sum_sq_x = 0;
		float sum_sq_y = 0;
		float sum_coproduct = 0;

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		float mean_x = mean(vector1);
		float mean_y = mean(vector2);

		for (int i = 0; i < vector1.length; i++) {

			float delta_x = 0, delta_y = 0;

			if (!Float.isNaN(vector1[i]))
				delta_x = vector1[i] - mean_x; // mean normalize
			if (!Float.isNaN(vector2[i]))
				delta_y = vector2[i] - mean_y;

			sum_sq_x += delta_x * delta_x;
			sum_sq_y += delta_y * delta_y;
			sum_coproduct += delta_x * delta_y;
		}
		float pop_sd_x = (float) Math.sqrt(sum_sq_x / vector1.length);
		float pop_sd_y = (float) Math.sqrt(sum_sq_y / vector1.length);
		float cov_x_y = sum_coproduct / vector1.length;
		correlation = cov_x_y / (pop_sd_x * pop_sd_y);

		return 1 - correlation; // convert to similarity measure
	}

	private static float mean(float[] values) {
		int n = 0;
		float sum = 0;
		boolean any = false;
		for (float v : values) {
			if (Float.isNaN(v)) {
				continue;
			}
			n++;
			sum += v;
			any = true;
		}
		if (!any)
			return 0;
		return sum / n;
	}
}
