/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.data;

import org.caleydo.core.util.function.IFloatIterator;

/**
 * interface describing a method that combines a list of floats to a single value, e.g. its mean
 *
 * @author Samuel Gratzl
 *
 */
public interface IFloatInferrer {
	/**
	 * combines the given list with the given size to a new float value
	 * 
	 * @return
	 */
	float infer(IFloatIterator it, int size);
}
