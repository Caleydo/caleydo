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
 * Euclidean distance measure, implements {@link IDistanceMeasure}.
 * 
 * @author Bernhard Schlegl
 */
public class EuclideanDistance
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {

		float distance = 0;
		float sum = 0;
		float temp_diff = 0;

		if (vector1.length != vector2.length) {
			System.out.println("length of vectors not equal!");
			return 0;
		}

		for (int i = 0; i < vector1.length; i++) {

			if (Float.isNaN(vector1[i]) || Float.isNaN(vector2[i]))
				temp_diff = 0;
			else
				temp_diff = vector1[i] - vector2[i];

			sum += (float) (Math.pow(temp_diff, 2));
		}

		distance = (float) Math.sqrt(sum);

		return distance;
	}
}
