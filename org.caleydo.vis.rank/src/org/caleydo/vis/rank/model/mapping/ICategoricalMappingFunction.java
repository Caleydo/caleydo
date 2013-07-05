/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mapping;

import org.caleydo.vis.rank.data.IFloatFunction;

/**
 * @author Samuel Gratzl
 *
 */
public interface ICategoricalMappingFunction<T> extends IFloatFunction<T> {

	ICategoricalMappingFunction<T> clone();

	void reset();

	/**
	 * @return
	 */
	boolean isComplexMapping();
}
