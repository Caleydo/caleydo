/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import org.caleydo.vis.lineup.model.IRow;

import com.google.common.base.Function;


/**
 * the current strategy up to now is to show just a single stratification but here the maximal value for a group score,
 *
 * therefore if we have a group score
 *
 * @author Samuel Gratzl
 *
 */
public class IntCombinerAdapter implements Function<IRow, Integer> {

	private final MaxGroupCombiner combiner;

	/**
	 * @param score
	 */
	public IntCombinerAdapter(MaxGroupCombiner combiner) {
		this.combiner = combiner;
	}

	@Override
	public Integer apply(IRow in) {
		Float r = combiner.apply(in);
		if (r == null || r.isNaN())
			return null;
		return r.intValue();
	}
}
