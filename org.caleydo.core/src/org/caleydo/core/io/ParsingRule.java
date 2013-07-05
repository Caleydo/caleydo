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

/**
 * <p>
 * Specifies a row or a continuous range of rows which are to be parsed with the same data type out of a delimited
 * text-file matrix.
 * </p>
 * <p>
 * There are three ways to specify a parsing region.
 * </p>
 * <ol>
 * <li>By specifying only a {@link #fromColumn} only this column is parsed</li>
 * <li>By specifying a {@link #fromColumn} and a {@link #toColum} all columns between the two, including the from and
 * the to column are parsed.</li>
 * <li>By specifying a {@link #fromColumn} and setting {@link #parseUntilEnd} true, everything from (and including)
 * {@link #fromColumn} to the last column is parsed.</li>
 * </ol>
 * <p>
 * The {@link #dataType} specified defines the data type for the internal storage. Legal data types are
 * "NATURAL_NUMBER", "UNIQUE_OBJECT" and "REAL_NUMBER", as listed in {@link EDataClass}.
 * </p>
 * <p>
 * It is often necessary to define multiple ParsingRules for a single source file. Multiple ParsingRules may result in
 * omitting individual columns, but no column may be specified more than once.
 * </p>
 *
 * @author Alexander Lex
 *
 */
@XmlType
public class ParsingRule implements Comparable<ParsingRule> {

	/**
	 * The first column to be parsed. Notice that we count starting with 0. Mandatory
	 */
	private Integer fromColumn = -1;

	/**
	 * <p>
	 * The last column to be parsed. Optional.
	 * </p>
	 * <p>
	 * If not specified, only the column specified in {@link #fromColumn} will be parsed.
	 * </p>
	 * <p>
	 * Only either this OR {@link #parseUntilEnd} may be specified, not both.
	 * </p>
	 */
	private int toColum = -1;

	/**
	 * If this is true then all columns from {@link #fromColumn} to the last column will be parsed. Defaults to false.
	 * Optional.
	 * <p>
	 * Only either this OR {@link #toColum} may be specified, not both.
	 * </p>
	 */
	private boolean parseUntilEnd = false;

	/**
	 * Information about the columns to be parsed. The general information must be valid for all columns defined in the
	 * rule. The column-specific information such as the columnID and the column number of the source file can not be
	 * set, but will be set automatically.
	 */
	private ColumnDescription columnDescripton;

	/**
	 *
	 * @param fromColumn
	 *            setter, see {@link #fromColumn}
	 */
	public void setFromColumn(int fromColumn) {
		this.fromColumn = fromColumn;
	}

	/**
	 * @return the fromColumn, see {@link #fromColumn}
	 */
	public int getFromColumn() {
		return fromColumn;
	}

	/**
	 * @param toColum
	 *            setter, see {@link #toColum}
	 */
	public void setToColumn(int toColum) {
		this.toColum = toColum;
	}

	/**
	 * @return the toColum, see {@link #toColum}
	 */
	public int getToColumn() {
		return toColum;
	}

	/**
	 * @param parseUntilEnd
	 *            setter, see {@link #parseUntilEnd}
	 */
	public void setParseUntilEnd(boolean parseUntilEnd) {
		this.parseUntilEnd = parseUntilEnd;
	}

	/**
	 * @return the parseUntilEnd, see {@link #parseUntilEnd}
	 */
	public boolean isParseUntilEnd() {
		return parseUntilEnd;
	}

	@Override
	public boolean equals(Object obj) {
		return fromColumn.equals(obj);
	}

	@Override
	public int hashCode() {
		return fromColumn;
	}

	@Override
	public int compareTo(ParsingRule compareTarget) {
		return fromColumn.compareTo(compareTarget.getFromColumn());
	}

	/**
	 * @param columnDescripton
	 *            setter, see {@link #columnDescripton}
	 */
	public void setColumnDescripton(ColumnDescription columnDescripton) {
		this.columnDescripton = columnDescripton;
	}

	/**
	 * @return the columnDescripton, see {@link #columnDescripton}
	 */
	public ColumnDescription getColumnDescripton() {
		return columnDescripton;
	}

	@Override
	public String toString() {
		String dataDescriptionString = columnDescripton.toString() + "]";

		if (toColum == -1 && parseUntilEnd == false)
			return "[" + fromColumn + dataDescriptionString;
		else if (toColum == -1 && parseUntilEnd == true)
			return "[" + fromColumn + " to end, " + dataDescriptionString;
		else
			return "[" + fromColumn + "-" + toColum + dataDescriptionString;
	}
}
