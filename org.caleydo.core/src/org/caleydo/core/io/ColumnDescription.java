/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;

/**
 * A parsing specification for a single column containing the number of the column, the {@link EDataClass} and the
 * {@link EDataType}.
 *
 * @author Alexander Lex
 *
 */
@XmlType
public class ColumnDescription {

	/** The number of the column to be parsed, starting with 0 */
	private int column;

	/** The meta-data of the column. Can be either different for every column or the same for all columns in a table */
	private DataDescription dataDescription;

	/**
	 * Default Constructor used for columns that are part of homogeneous datasets.
	 */
	public ColumnDescription() {
	}

	/**
	 * Constructor for columns that are part of homogeneous datasets.
	 *
	 * @param column
	 *            sets {@link #column}.
	 */
	public ColumnDescription(int column) {
		this.column = column;
	}

	/**
	 * Constructor for inhomogeneous tables.
	 *
	 * @param dataClass
	 *            sets {@link #dataDescription}
	 */
	public ColumnDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;

	}

	/**
	 * Constructor fully initializing the object, for inhomogeneous tables.
	 *
	 * @param column
	 *            sets {@link #column}
	 * @param dataClass
	 *            sets {@link #dataDescription}
	 */
	public ColumnDescription(int column, DataDescription dataDescription) {
		this.column = column;
		this.dataDescription = dataDescription;
	}

	/**
	 * @param column
	 *            setter, see {@link #column}
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @return the column, see {@link #column}
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @param dataDescription
	 *            setter, see {@link dataDescription}
	 */
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
	}

	/**
	 * @return the dataDescription, see {@link #dataDescription}
	 */
	public DataDescription getDataDescription() {
		return dataDescription;
	}

	@Override
	public String toString() {
		return "[" + column + ", " + dataDescription + "]";
	}
}
