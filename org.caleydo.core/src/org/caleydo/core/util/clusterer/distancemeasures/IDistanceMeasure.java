/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.distancemeasures;

import org.caleydo.core.util.function.IDoubleSizedIterable;

/**
 * Interface class for all self implemented distance measures.
 *
 * @author Bernhard Schlegl
 */
public interface IDistanceMeasure {

	/**
	 * Calculates the distance measure between to given vectors and returns the result.
	 *
	 * @param vector1
	 *            first vector
	 * @param vector2
	 *            second vector
	 * @return result of distance measure calculation
	 */
	float getMeasure(float[] vector1, float[] vector2);

	double apply(IDoubleSizedIterable a, IDoubleSizedIterable b);
}
