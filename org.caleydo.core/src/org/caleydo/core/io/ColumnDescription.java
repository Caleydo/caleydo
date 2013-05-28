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
