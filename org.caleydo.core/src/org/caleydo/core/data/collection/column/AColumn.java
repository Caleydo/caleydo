/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column;

import java.util.HashMap;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IContainer;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.io.DataDescription;

/**
 * <p>
 * Base implementation for columns of data stored in a {@link Table}. A column is a container that holds various
 * representations of a raw data column as read from a source file. Most importantly it contains the raw data as well as
 * as transformed and normalized version(s) of the raw data. It uses {@link IContainer} implementations to store this
 * data.
 * </p>
 * <p>
 * Only the raw data and some metadata can be specified manually, the rest is computed on on demand.
 * </p>
 * <p>
 * This class provides only the base functionality. Data type specific implementations such as {@link NumericalColumn},
 * {@link CategoricalColumn} and {@link GenericColum} exist.
 *
 * @author Alexander Lex
 */

public abstract class AColumn<RawContainerType extends IContainer<RawType>, RawType> {

	/** The class of data stored in this column */
	private EDataClass dataClass;

	/** The data type of the raw data of this column */
	private EDataType rawDataType;

	/** The default transformation of this column */
	private String defaultDataTransformation = Table.Transformation.LINEAR;

	/** The id of this column, corresponds to the index of the column in the table */
	private int id;

	/** The container holding the raw data */
	protected RawContainerType rawContainer;

	/** A map of the string identifying a transformation to the transformed and normalized */
	protected HashMap<String, FloatContainer> dataRepToContainerMap;

	/**
	 * Constructor Initializes objects
	 */
	public AColumn(DataDescription dataDescription) {
		dataRepToContainerMap = new HashMap<>();
		this.dataClass = dataDescription.getDataClass();
		if (dataDescription.getNumericalProperties() != null) {
			this.defaultDataTransformation = dataDescription.getNumericalProperties().getDataTransformation();
		}
		this.rawDataType = dataDescription.getRawDataType();
	}

	/**
	 * @param id
	 *            setter, see {@link id}
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}

	/**
	 * Returns the data type of the raw data
	 *
	 * @return a value of ERawDataType
	 */
	public EDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * Set the raw data with data type float
	 *
	 * @param rawData
	 *            a float array containing the raw data
	 */
	public void setRawData(RawContainerType rawContainer) {
		assert this.rawContainer == null : "Raw data was already set in column " + id + " , tried to set again.";
		assert rawContainer.getDataType().equals(rawDataType) : "Raw data in container and in column don't match";

		this.rawContainer = rawContainer;
	}

	/**
	 * @param defaultDataTransformation
	 *            setter, see {@link defaultDataTransformation}
	 */
	public void setDefaultDataTransformation(String defaultDataTransformation) {
		this.defaultDataTransformation = defaultDataTransformation;
	}

	/**
	 * @return the defaultDataTransformation, see {@link #defaultDataTransformation}
	 */
	public String getDefaultDataTransformation() {
		return defaultDataTransformation;
	}

	/**
	 * Same as {@link #getNormalizedValue(String, int)} but uses the {@link #defaultDataTransformation} instead of a
	 * parameter.
	 *
	 * @param index
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public float getNormalizedValue(int index) {
		return getNormalizedValue(defaultDataTransformation, index);
	}

	/**
	 * Returns the normalized float value from the index in this column.
	 *
	 * @param dataTransformation
	 *            the transformation that should be used
	 * @param index
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public float getNormalizedValue(String dataTransformation, int index) {
		return dataRepToContainerMap.get(dataTransformation).getPrimitive(index);
	}

	public RawType getRaw(int index) {
		return rawContainer.get(index);
	}

	public String getRawAsString(int index) {
		return rawContainer.get(index).toString();
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
	 * Creates all transformations and normalizes them into a format between 0 and 1
	 */
	public void normalize() {
		dataRepToContainerMap.put(Table.Transformation.LINEAR, rawContainer.normalize());
	}

	/**
	 * Returns meta-data on the column if it is defined for the column type.
	 *
	 * @return
	 */
	public Object getDataClassSpecificDescription() {
		return null;
	}

	/**
	 * @return the dataClass, see {@link #dataClass}
	 */
	public EDataClass getDataClass() {
		return dataClass;
	}
}
