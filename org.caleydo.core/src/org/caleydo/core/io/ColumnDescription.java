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

	/**
	 * The dataClass of the column, must be one equivalent to those listed in {@link EDataClass}. Defaults to real
	 * numbers.
	 */
	private EDataClass dataClass = EDataClass.REAL_NUMBER;

	/**
	 * The data type of the {@link #dataClass}. If the dataClass has only one possible dataType (see {@link EDataClass})
	 * this is automatically set. Defaults to float.
	 */
	private EDataType dataType = EDataType.FLOAT;

	/**
	 * An integer ID of the column. For newly loaded data this needs not be set. After serializing the data however,
	 * this is the way we re-assign the same columnID to the same column again.
	 */
	// private Integer columnID = null;

	/**
	 * Default Constructor, creates a ColumnDescripton with float dataClass and continuous columnType and no column
	 * number for parsing.
	 */
	public ColumnDescription() {
	}

	/**
	 * Constructor fully initializing the object.
	 *
	 * @param column
	 *            see {@link #column}
	 * @param dataClass
	 *            see {@link #dataClass}
	 */
	public ColumnDescription(int column, EDataClass dataClass, EDataType dataType) {
		this.column = column;
		this.dataClass = dataClass;
		if (!dataClass.supports(dataType))
			throw new IllegalArgumentException("DataClass " + dataClass + " doesn't support dataType " + dataType);
		this.dataType = dataType;
	}

	/**
	 * Constructor specifying the types of the column but not the parsing information
	 *
	 * @param dataClass
	 * @param columnType
	 */
	public ColumnDescription(EDataClass dataClass, EDataType dataType) {
		this.dataClass = dataClass;
		this.dataType = dataType;
	}

	/**
	 * Constructor specifying the class of column and infers the data type from the class if possible. *
	 *
	 * @param dataClass
	 * @throws IllegalArgumentException
	 *             if inference is not obvious.
	 */
	public ColumnDescription(EDataClass dataClass) {
		if (dataClass.getSupportedDataType() == null)
			throw new IllegalArgumentException("Cannot infer data type for data class " + dataClass);
		this.dataClass = dataClass;
		this.dataType = dataClass.getSupportedDataType();
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
	 * @param dataClass
	 *            setter, see {@link dataClass}
	 */
	public void setDataClass(EDataClass dataClass) {
		this.dataClass = dataClass;
		if (dataClass.getSupportedDataType() != null)
			dataType = dataClass.getSupportedDataType();
	}

	/**
	 * @return the dataClass, see {@link #dataClass}
	 */
	public EDataClass getDataClass() {
		return dataClass;
	}

	/**
	 * @param dataType
	 *            setter, see {@link dataType}
	 */
	public void setDataType(EDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataType, see {@link #dataType}
	 */
	public EDataType getDataType() {
		return dataType;
	}

	// /**
	// * @param columnID
	// * setter, see {@link #columnID}
	// */
	// public void setColumnID(Integer columnID) {
	// this.columnID = columnID;
	// }
	//
	// /**
	// * @return the columnID, see {@link #columnID}
	// */
	// public Integer getColumnID() {
	// return columnID;
	// }

	@Override
	public String toString() {
		return "[" + column + ", " + dataClass + "]";
	}
}
