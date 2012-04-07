/**
 * 
 */
package org.caleydo.core.data.importing;

import javax.xml.bind.annotation.XmlType;

/**
 * @author alexsb
 * 
 */
@XmlType
public class ParsingDetails {

	private int column;
	private String dataType;

	/**
	 * 
	 */
	public ParsingDetails() {
		// TODO Auto-generated constructor stub
	}

	public ParsingDetails(int column, String dataType) {
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
}
