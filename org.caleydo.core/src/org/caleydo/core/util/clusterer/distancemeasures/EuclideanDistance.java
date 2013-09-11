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
 * Euclidean distance measure, implements {@link IDistanceMeasure}.
 *
 * @author Bernhard Schlegl
 */
public class EuclideanDistance
	implements IDistanceMeasure {

	@Override
	public float getMeasure(float[] vector1, float[] vector2) {
		if (vector1.length != vector2.length) {
			System.err.println("length of vectors not equal!");
			return 0;
		}

		double sum = 0;
		float temp_diff = 0;
		for (int i = 0; i < vector1.length; i++) {
			if (Float.isNaN(vector1[i]) || Float.isNaN(vector2[i]))
				temp_diff = 0;
			else
				temp_diff = vector1[i] - vector2[i];

			sum += Math.pow(temp_diff, 2);
		}

		float distance = (float) Math.sqrt(sum);

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
				acc += (a_d - b_d) * (a_d - b_d);
		}
		return Math.sqrt(acc);
	}
}
