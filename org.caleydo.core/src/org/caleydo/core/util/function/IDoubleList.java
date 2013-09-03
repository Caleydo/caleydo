/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.List;

/**
 * a special version of a {@link List} for double handling
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleList extends List<Double>, IDoubleSizedIterable {
	/**
	 * returns the primitive version of {@link #get(int)}
	 *
	 * @param index
	 * @return
	 */
	double getPrimitive(int index);

	/**
	 * returns a view of this list where, the given function will be applied
	 *
	 * @param f
	 * @return
	 */
	IDoubleListView map(IDoubleFunction f);

	/**
	 * return a {@link IDoubleList} only with the elements matching the {@link IDoublePredicate}
	 *
	 * @param p
	 * @return
	 */
	IDoubleList filter(IDoublePredicate p);

	/**
	 * reduces/fold the data using the given start value and the given {@link IDoubleReduction} function
	 *
	 * @param start
	 * @param r
	 * @return
	 */
	double reduce(double start, IDoubleReduction r);

	@Override
	IDoubleSizedIterator iterator();

	/**
	 * @return a fresh copy of the data as primitive array
	 */
	double[] toPrimitiveArray();
}
