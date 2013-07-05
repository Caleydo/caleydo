/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.io.parser.ascii.GroupingParser;

/**
 * <p>
 * Specifies how to parse a grouping file to ultimately create {@link GroupList}
 * The parsing according to the rules specified here is done in
 * {@link GroupingParser}.
 * </p>
 * <p>
 * The data is assumed to be available in a delimited text file and follow the
 * contract specified in the base class {@link MatrixDefinition}. Here is an
 * example for a valid grouping file, with a header line:
 * 
 * <pre>
 * {@code 
 * ID    g1 g2
 * id_1  a  b
 * id_2  a  b
 * id_3  b  a
 *  }
 * </pre>
 * 
 * </p>
 * <p>
 * Groupings are based on the following assumptions:
 * </p>
 * <ul>
 * <li>One column of the source file contains an identifier of the ID to be
 * grouped (the first column in the above example).</li>
 * <li>Other columns contain a group association for every line. Group
 * associations are arbitrary strings (except for the delimiter).</li>
 * <li>If the group association for two IDs are identical (according to
 * {@link String#equals(Object)}), they belong to the same group</li>
 * </ul>
 * 
 * <p>
 * By default, it is assumed that the first column contains the row IDs and all
 * other columns contain groupings. If one of these assumptions does not hold,
 * both, the exact column of the ids ({@link #setColumnOfRowIds(Integer)}) and
 * the exact columns of the groupings ({@link #setColumns(ArrayList)} or
 * {@link #addColum(Integer)}) can be specified. Note that if you are not using
 * the default, you need to specify <b>both, the column of the ids and the ids
 * of all groupings.</b>
 * </p>
 * 
 * <p>
 * If the file contains multiple columns, all of them are loaded as a separate
 * grouping. This can be overridden to specific columns using the
 * {@link #columns} parameter.
 * </p>
 * 
 * <p>
 * By default, it is assumed that the groupings contain a header inkluding a
 * name for the group (see the {@link MatrixDefinition} for details. This can be
 * changed by setting {@link #setContainsColumnIDs(boolean)} to false. In this
 * case the group names area automatically generated based on the number of
 * groups.
 * </p>
 * 
 * <p>
 * In case of automatically created grouping names a global name can be
 * specified using
 * </p>
 * 
 * @author Alexander Lex
 * 
 */
@XmlType
public class GroupingParseSpecification
	extends MatrixDefinition {

	/**
	 * Optional parameter to specify the columns of the grouping in the
	 * delimited file. If no value is set, all columns are loaded.
	 */
	private ArrayList<Integer> columns;

	/**
	 * Optional name for all groupings in the specification. Only has an effect
	 * if {@link #isContainsColumnIDs()} is false. In this case the name of a
	 * grouping is this name supplemented by the number of groups (eg
	 * "myName_8");
	 */
	private String groupingName;

	/**
	 * Default Constructor, use setters to specify data
	 */
	public GroupingParseSpecification() {
	}

	/**
	 * Constructor with {@link #path} as argument.
	 */
	public GroupingParseSpecification(String dataSourcePath) {
		this.dataSourcePath = dataSourcePath;
	}

	/**
	 * Sets the columns to be parsed. Overwrites everything previously set
	 * 
	 * @param columns setter, see {@link #columns}
	 */
	public void setColumns(ArrayList<Integer> columns) {
		this.columns = columns;
	}

	/** Add a single column to the existing ones */
	public void addColum(Integer column) {
		if (columns == null) {
			columns = new ArrayList<Integer>();
		}
		columns.add(column);
	}

	/**
	 * @return the column, see {@link #column}
	 */
	public ArrayList<Integer> getColumns() {
		return columns;
	}

	/**
	 * @param groupingName setter, see {@link #groupingName}
	 */
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	/**
	 * @return the groupingName, see {@link #groupingName}
	 */
	public String getGroupingName() {
		return groupingName;
	}

}
