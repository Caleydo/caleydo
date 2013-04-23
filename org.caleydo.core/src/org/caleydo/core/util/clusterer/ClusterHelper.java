/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer;

import java.util.Arrays;

/**
 * Cluster helper provides methods needed in cluster algorithms such as median, arithmetic mean, etc.
 *
 * @author Bernhard Schlegl
 */
public class ClusterHelper {

	/**
	 * Calculates the median for a given vector (float array)
	 *
	 * @param vector
	 * @return median
	 */
	public static float median(float[] vector) {
		float median = 0;
		float[] temp = new float[vector.length];

		for (int i = 0; i < temp.length; i++) {

			if (Float.isNaN(vector[i]))
				temp[i] = 0;
			else
				temp[i] = vector[i];
		}

		Arrays.sort(temp);

		if ((temp.length % 2) == 0)
			median = (temp[(int) Math.floor(temp.length / 2)] + temp[(int) Math.floor((temp.length + 1) / 2)]) / 2;
		else
			median = temp[(int) Math.floor((temp.length + 1) / 2)];

		return median;
	}

}
