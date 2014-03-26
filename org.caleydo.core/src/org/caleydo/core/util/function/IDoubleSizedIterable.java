/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

/**
 * a {@link Iterable} for doubles providing a {@link IDoubleSizedIterator} iterator and a size
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleSizedIterable extends Iterable<Double> {
	/**
	 * return the size of the items
	 *
	 * @return
	 */
	int size();

	@Override
	IDoubleSizedIterator iterator();

	/**
	 * maps the data using the given function
	 * 
	 * @param f
	 * @return
	 */
	IDoubleSizedIterable map(IDoubleFunction f);
}
