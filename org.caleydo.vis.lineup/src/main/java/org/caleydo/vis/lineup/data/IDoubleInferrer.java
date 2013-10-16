/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.data;

import org.caleydo.core.util.function.IDoubleSizedIterator;

/**
 * interface describing a method that combines a list of floats to a single value, e.g. its mean
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleInferrer {
	/**
	 * combines the given list with the given size to a new float value
	 *
	 * @return
	 */
	double infer(IDoubleSizedIterator it);
}
