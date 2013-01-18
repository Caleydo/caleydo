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

import org.caleydo.core.data.collection.EDataType;

/**
 * <p>
 * Interface for the low level containers which hold the data in the columns.
 * </p>
 * <p>
 * Containers can not be changed once they are initialized and can only be initialized once bye either iteratively
 * calling {@link #add(Object)} (or primitive equivalents) or by passing an array through a constructor.
 * </p>
 * <p>
 * The generic DataType argument either directly defines the used data type, or, in the case where primitive equivalents
 * exist (int, float, double) provides boxed access to the primitive underlying data types. Generally, it is encouraged
 * to use the primitive equivalents if a concrete implementation is available. *
 *
 * @author Alexander Lex
 */
public interface IContainer<DataType> {

	/**
	 * @return the size of the container
	 */
	public int size();

	/**
	 * Creates a {@link FloatContainer} instance that contains a normalized version of the data into the unit interval
	 * [0..1].
	 */
	public FloatContainer normalize();

	/** Returns the data type used in the container */
	public EDataType getDataType();

	/**
	 * <p>
	 * Generic interface to access a value at a specific index.
	 * </p>
	 * <p>
	 * Primitive data type implementations should provide an overridden version returning the primitive type (e.g.,
	 * public float getValue(int index)) to avoid boxing.
	 * </p>
	 *
	 * @param index
	 */
	public DataType get(int index);

	/**
	 * <p>
	 * Generic interface to append a value.
	 * </p>
	 * <p>
	 * Primitive data type implementations should provide an overridden version using the primitive type (e.g., public
	 * float addValue(float value)) to avoid boxing.
	 * </p>
	 *
	 * @param value
	 *            the value to be appended.
	 */
	public void add(DataType value);

}
