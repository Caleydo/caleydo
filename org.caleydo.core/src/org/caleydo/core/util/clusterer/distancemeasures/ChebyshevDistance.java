/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.distancemeasures;

/**
 * Chebyshev distance measure, implements {@link IDistanceMeasure}.
 * 
 * @author Bernhard Schlegl
 */
public class ChebyshevDistance
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

			distance = Math.max(distance, temp_diff);
		}

		return distance;
	}
}
