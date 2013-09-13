/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.distancemeasures;

import static java.lang.Double.isNaN;

import org.caleydo.core.util.function.IDoubleSizedIterable;
import org.caleydo.core.util.function.IDoubleSizedIterator;

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

	@Override
	public double apply(final IDoubleSizedIterable a, final IDoubleSizedIterable b) {
		final IDoubleSizedIterator a_it = a.iterator();
		final IDoubleSizedIterator b_it = a.iterator();

		double acc = 0;

		while (a_it.hasNext() && b_it.hasNext()) {
			double a_d = a_it.nextPrimitive();
			double b_d = b_it.nextPrimitive();
			if (!isNaN(a_d) && !isNaN(b_d))
				acc += Math.abs(a_d - b_d);
		}
		return acc;
	}
}
