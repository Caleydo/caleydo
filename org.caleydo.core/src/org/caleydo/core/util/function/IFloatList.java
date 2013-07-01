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

	IFloatList filter(IFloatPredicate p);

	float reduce(float start, IFloatReduction r);

	@Override
	IFloatIterator iterator();

	float[] toPrimitiveArray();
}
