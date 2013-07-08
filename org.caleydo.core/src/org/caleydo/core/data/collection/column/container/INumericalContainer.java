/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column.container;

/**
 * Extension of IContainer for numerical data.
 *
 * @author Alexander Lex
 */
public interface INumericalContainer<DATA_TYPE extends Number> extends IContainer<DATA_TYPE> {

	/**
	 * <p>
	 * Creates a {@link FloatContainer} instance that contains a normalized version of the data into the unit interval
	 * [0..1] where the parameter min corresponds to the lower bound (0) and max to the upper bound (1).
	 * </p>
	 * <p>
	 * If values in the container exceed max or are smaller than min they are written to 1 and 0 respectively anyways.
	 * </p>
	 *
	 * @param the
	 *            artificial minimum
	 * @param the
	 *            artificial maximum
	 * @return the normalized equivalent of this container
	 */
	public FloatContainer normalizeWithAtrificalExtrema(double min, double max);

	/**
	 * Returns the minimum of the container
	 *
	 * @return the minimum
	 */
	public double getMin();

	/**
	 * Returns the maximum of the container
	 *
	 * @return the maximum
	 */
	public double getMax();

	/**
	 * Creates a {@link FloatContainer} containing a logarithmic representation (base as passed as parameter) for the
	 * data.
	 *
	 * @return the logarithmized equivalent of this container
	 */
	public FloatContainer log(int base);

}
