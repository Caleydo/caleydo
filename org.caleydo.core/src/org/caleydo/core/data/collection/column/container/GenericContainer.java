/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.collection.EDataType;

/**
 * Container for generic, unstructured data, such as Unique Strings, images, etc.
 *
 * @author Alexander Lex
 */
public class GenericContainer<DATA_TYPE> implements IContainer<DATA_TYPE> {

	private final List<DATA_TYPE> container;
	private EDataType dataType;

	/**
	 *
	 */
	public GenericContainer(int size) {
		container = new ArrayList<>(size);
	}

	@Override
	public int size() {
		return container.size();
	}

	@Override
	public Iterator<DATA_TYPE> iterator() {
		return new ContainerIterator<>(this);
	}

	@Override
	public FloatContainer normalize() {
		FloatContainer normalized = new FloatContainer(container.size());

		float normalizedValue = 0;
		float increment = 1 / (container.size() - 1);
		for (int i = 0; i < container.size(); i++) {
			normalized.add(normalizedValue);
			normalizedValue += increment;
		}

		return normalized;
	}

	/**
	 * @param dataType
	 *            setter, see {@link dataType}
	 */
	public void setDataType(EDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public EDataType getDataType() {
		return dataType;
	}

	@Override
	public DATA_TYPE get(int index) {
		return container.get(index);
	}

	@Override
	public void add(DATA_TYPE value) {
		container.add(value);

	}

	@Override
	public void addUnknown() {
		container.add(null);
	}

	@Override
	public boolean isUnknown(DATA_TYPE value) {
		return value == null;
	}

}
