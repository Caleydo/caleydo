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
package org.caleydo.core.data.importing;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EColumnType;

/**
 * A parsing specification for a single column containing the number of the
 * column and the data type.
 * 
 * @author Alexander Lex
 * 
 */
@XmlType
public class ColumnParsingDetail {

	/** The number of the column to be parsed, starting with 0 */
	private int column;
	/**
	 * The dataType of the column, must be one equivalent to those listed in
	 * {@link EColumnType}
	 */
	private String dataType;

	/**
	 * An integer ID of the column. For newly loaded data this needs not be set.
	 * After serializing the data however, this is the way we re-assign the same
	 * columnID to the same column again.
	 */
	private Integer columnID = null;

	/**
	 * Default Constructor
	 */
	public ColumnParsingDetail() {
	}

	/**
	 * Constructor fully initializing the object.
	 * 
	 * @param column
	 *            see {@link #column}
	 * @param dataType
	 *            see {@link #dataType}
	 */
	public ColumnParsingDetail(int column, String dataType) {
		this.column = column;
		this.dataType = dataType;
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
