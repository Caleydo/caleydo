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
package org.caleydo.core.data.collection.column;

import java.util.HashMap;

import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IContainer;
import org.caleydo.core.util.base.AUniqueObject;

/**
 * Interface for Dimensions A Dimension is a container that holds various representations of a particular data entity,
 * for example a microarray experiment, or a column on illnesses in a clinical data file. It contains all information
 * considering one such entity, for example, the raw, normalized and logarithmized data as well as metadata, such as the
 * label of the experiment. Only the raw data and some metadata can be specified manually, the rest is computed on on
 * demand. One distinguishes between two basic dimension types: numerical and nominal. This is reflected in the two
 * sub-interfaces INumericalSet and INominalSet. After construction one of the setRawData methods has to be called.
 * Notice, that only one setRawData may be called exactly once, since a set is designed to contain only one raw data set
 * at a time.
 *
 * @author Alexander Lex
 */

public abstract class AColumn<RawContainerType extends IContainer<RawType>, RawType> extends AUniqueObject {

	protected RawContainerType rawContainer;
	protected FloatContainer normalizedContainer;

	protected HashMap<String, IContainer<?>> dataRepToContainerMap;

	private ERawDataType rawDataType = ERawDataType.UNDEFINED;

	/**
	 * Constructor Initializes objects
	 */
	public AColumn(int uniqueID) {
		super(uniqueID);

		dataRepToContainerMap = new HashMap<String, IContainer<?>>();
	}

	/**
	 * Returns the data type of the raw data
	 *
	 * @return a value of ERawDataType
	 */
	public ERawDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * Set the raw data with data type float
	 *
	 * @param rawData
	 *            a float array containing the raw data
	 */
	public void setRawData(RawContainerType rawContainer) {
		if (this.rawContainer != null)
			throw new IllegalStateException("Raw data was already set in Dimension " + uniqueID
					+ " , tried to set again.");
		this.rawContainer = rawContainer;
	}

	public boolean containsDataRepresentation(DataRepresentation dataRepresentation) {
		return dataRepToContainerMap.containsKey(dataRepresentation);
	}

	/**
	 * Returns the normalized float value from the index in this column.
	 *
	 * @param index
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public float getNormalizedValue(int index) {
		return normalizedContainer.get(index);
	}

	public RawType getRaw(int index) {
		return rawContainer.getValue(index);
	}

	public String getRawAsString(int index) {
		return rawContainer.getValue(index).toString();
	}


	/**
	 * Returns the number of raw data elements
	 *
	 * @return the number of raw data elements
	 */
	public int size() {
		return rawContainer.size();
	}

	@Override
	public String toString() {
		return "Dimension for " + getRawDataType() + ", size: " + size();
	}

	/**
	 * Brings any dataset into a format between 0 and 1. This is used for drawing. Works for nominal and numerical data.
	 * Operates with the raw data as basis by default, however when a logarithmized representation is in the dimension
	 * this is used (only applies to numerical data). For nominal data the first value is 0, the last value is 1
	 */
	public abstract void normalize();

}
