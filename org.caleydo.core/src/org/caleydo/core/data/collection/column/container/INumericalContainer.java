/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
