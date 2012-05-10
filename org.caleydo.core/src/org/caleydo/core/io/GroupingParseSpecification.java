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
 * contract specified in the base class {@link MatrixDefinition}
 * </p>
 * <p>
 * Groupings are based on the following assumptions:
 * </p>
 * <ul>
 * <li>Groupings are based on an identifier-group relationship, where one group
 * is defined in a column.</li>
 * <li>One column contains an identifier of the ID to be grouped.</li>
 * <li>Other columns contain a key specifying the group the ID belongs to. The
 * key can be an arbitrary string (except for the delimiter).</li>
 * <li>If the keys for two IDs are identical (according to
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
 * @author Alexander Lex
 * 
 */
@XmlType
public class GroupingParseSpecification extends MatrixDefinition {

	/**
	 * Optional parameter to specify the column of the grouping in the delimited
	 * file. If no value is set, all columns are loaded.
	 */
	private ArrayList<Integer> columns;

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
	 * @param columns
	 *            setter, see {@link #columns}
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

}
