/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
public interface IContainer<DATA_TYPE> extends Iterable<DATA_TYPE> {

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
	public DATA_TYPE get(int index);

	/**
	 * <p>
	 * Generic interface to append a value.
	 * </p>
	 * <p>
	 * Primitive data type implementations should provide an overridden version using the primitive type (e.g., public
	 * float addValue(float value)) to avoid boxing.
	 * </p>
	 *
	 * @see {@link #addUnknown()} for adding an entry of an unknown type, e.g., due to a parsing error
	 *
	 * @param value
	 *            the value to be appended.
	 * @throws IndexOutOfBoundsException
	 *             if next index of container is larger then {@link #size()}
	 */
	public void add(DATA_TYPE value) throws IndexOutOfBoundsException;



	/**
	 * Add an entry of unknown value, e.g., due to a parsing error or a missing entry.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if next index of container is larger then {@link #size()}
	 */
	public void addUnknown() throws IndexOutOfBoundsException;

	/**
	 * returns whether the given is is the unknown value
	 *
	 * @param value
	 * @return
	 */
	public boolean isUnknown(DATA_TYPE value);

}
