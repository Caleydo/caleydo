/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.List;

/**
 * a special version of a {@link List} for float handling
 *
 * @author Samuel Gratzl
 *
 */
public interface IFloatList extends List<Float> {
	/**
	 * returns the primitive version of {@link #get(int)}
	 *
	 * @param index
	 * @return
	 */
	float getPrimitive(int index);

	/**
	 * returns a view of this list where, the given function will be applied
	 *
	 * @param f
	 * @return
	 */
	IFloatListView map(IFloatFunction f);

	/**
	 * return a {@link IFloatList} only with the elements matching the {@link IFloatPredicate}
	 *
	 * @param p
	 * @return
	 */
	IFloatList filter(IFloatPredicate p);

	/**
	 * reduces/fold the data using the given start value and the given {@link IFloatReduction} function
	 *
	 * @param start
	 * @param r
	 * @return
	 */
	float reduce(float start, IFloatReduction r);

	@Override
	IFloatIterator iterator();

	/**
	 * @return a fresh copy of the data as primitive array
	 */
	float[] toPrimitiveArray();
}
