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

import java.util.ArrayList;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IContainer;

/**
 * Container for generic, unstructured data, such as Unique Strings, images, etc.
 * 
 * @author Alexander Lex
 */
public class GenericContainer<DataType> implements IContainer<DataType> {

	ArrayList<DataType> container;
	EDataType dataType;

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
	public DataType get(int index) {
		return container.get(index);
	}

	@Override
	public void add(DataType value) {
		container.add(value);

	}

}
