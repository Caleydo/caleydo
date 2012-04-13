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
