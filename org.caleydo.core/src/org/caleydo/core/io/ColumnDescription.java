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
/**
 * 
 */
package org.caleydo.core.io;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataType;

/**
 * A parsing specification for a single column containing the number of the
 * column and the data type.
 * 
 * @author Alexander Lex
 * 
 */
@XmlType
public class ColumnDescription {

	public static final String CONTINUOUS = "continuous";
	public static final String ORDINAL = "ordinal";
	public static final String NOMINAL = "nominal";

	/** The number of the column to be parsed, starting with 0 */
	private int column;

	/**
	 * The dataType of the column, must be one equivalent to those listed in
	 * {@link EDataType}. Defaults to float.
	 */
	private String dataType = "FLOAT";

	/**
	 * The type of data found in the column. We distinguish between
	 * {@link #CONTINUOUS} (real numbers, integers), {@link #ORDINAL} (ordered
	 * categories) and {@link #NOMINAL} (unordered categories). Defaults to
	 * continuous.
	 */
	private String columnType = CONTINUOUS;

	/**
	 * An integer ID of the column. For newly loaded data this needs not be set.
	 * After serializing the data however, this is the way we re-assign the same
	 * columnID to the same column again.
	 */
	private Integer columnID = null;

	/**
	 * Default Constructor, creates a ColumnDescripton with float dataType and
	 * continuous columnType and no column number for parsing.
	 */
	public ColumnDescription() {
	}

	/**
	 * Constructor fully initializing the object.
	 * 
	 * @param column
	 *            see {@link #column}
	 * @param dataType
	 *            see {@link #dataType}
	 */
	public ColumnDescription(int column, String dataType, String columnType) {
		this.column = column;
		this.dataType = dataType;
		this.columnType = columnType;
	}

	/**
	 * Constructor specifying the types of the column but not the parsing
	 * information
	 * 
	 * @param dataType
	 * @param columnType
	 */
	public ColumnDescription(String dataType, String columnType) {
		this.dataType = dataType;
		this.columnType = columnType;
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
	 * @param dataType
	 *            setter, see {@link #dataType}
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataType, see {@link #dataType}
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param columnType
	 *            setter, see {@link #columnType}
	 */
	public void setColumnType(String columnType) {
		if (columnType.equalsIgnoreCase(CONTINUOUS)
				|| columnType.equalsIgnoreCase(ORDINAL)
				|| columnType.equalsIgnoreCase(NOMINAL))
			this.columnType = columnType;
		else
			throw new IllegalStateException("Unknown column type: " + columnType);
	}

	/**
	 * @return the columnType, see {@link #columnType}
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * @param columnID
	 *            setter, see {@link #columnID}
	 */
	public void setColumnID(Integer columnID) {
		this.columnID = columnID;
	}

	/**
	 * @return the columnID, see {@link #columnID}
	 */
	public Integer getColumnID() {
		return columnID;
	}

	@Override
	public String toString() {
		return "[" + column + ", " + dataType + "]";
	}
}
