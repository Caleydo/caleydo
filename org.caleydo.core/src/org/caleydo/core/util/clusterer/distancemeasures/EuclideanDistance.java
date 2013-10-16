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
		final int length = vector1.length;
		if (length != vector2.length) {
			System.err.println("length of vectors not equal!");
			return 0;
		}

		float sum = 0;
		for (int i = 0; i < length; i++) {
			float d = vector1[i] - vector2[i];
			if (!Float.isNaN(d))
				sum += d * d; // multiplication is faster than pow(x,2)
		}

		return (float) Math.sqrt(sum);
	}

	@Override
	public double apply(final IDoubleSizedIterable a, final IDoubleSizedIterable b) {
		final IDoubleSizedIterator a_it = a.iterator();
		final IDoubleSizedIterator b_it = a.iterator();

		double acc = 0;

		while (a_it.hasNext() && b_it.hasNext()) {
			double d = a_it.nextPrimitive() - b_it.nextPrimitive();
			if (!isNaN(d))
				acc += d * d;
		}
		return Math.sqrt(acc);
	}
}
