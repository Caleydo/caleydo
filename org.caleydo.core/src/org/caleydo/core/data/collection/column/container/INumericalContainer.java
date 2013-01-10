/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.column.container;

/**
 * Extension of the ICContainer interface for numerical values
 *
 * @author Alexander Lex
 */
public interface INumericalContainer<DataType> extends IContainer<DataType> {

	/**
	 * Execute the normalize method, where values in the container are normalized to values between 0 and 1,
	 * but do not take min max from the range calculated internally, but use those specified in min, max
	 * Take care that min and max are smaller resp. bigger than the smallest resp. biggest value in the
	 * data.
	 *
	 * @param min
	 *            the minimum
	 * @param max
	 *            the maximum
	 * @return a container with the normalized values
	 * @throws IllegalAttributeException
	 *             when iMin is >= iMax
	 */
	public FloatContainer normalizeWithExternalExtrema(double dMin, double dMax);

	/**
	 * Returns the minimum of the container, double to fit all datatypes
	 *
	 * @return the minimum
	 */
	public double getMin();

	/**
	 * Returns the maximum of the container, double to fit all datatypes
	 *
	 * @return the maximum
	 */
	public double getMax();

	// /**
	// * Calculates a logarithmic representation (logarithm to the base of 10)
	// for
	// * the data, which it returns as a new ICContainer
	// *
	// * @return
	// */
	// public FloatCContainer log10();

	/**
	 * Calculates a logarithmic representation of a base for the data, which it returns as a new ICContainer
	 *
	 * @return
	 */
	public FloatContainer log(int base);

}
