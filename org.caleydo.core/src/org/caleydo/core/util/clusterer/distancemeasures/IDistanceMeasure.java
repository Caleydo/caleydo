/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.distancemeasures;

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
	public float getMeasure(float[] vector1, float[] vector2);

}
