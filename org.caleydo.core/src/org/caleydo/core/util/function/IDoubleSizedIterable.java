/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDoubleSizedIterable extends Iterable<Double> {
	int size();

	@Override
	IDoubleSizedIterator iterator();

	IDoubleSizedIterable map(IDoubleFunction f);
}
