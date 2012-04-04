/**
 * 
 */
package org.caleydo.core.parser.ascii;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.group.GroupList;

/**
 * <p>
 * Specifies how to parse a grouping file to ultimately create {@link GroupList}
 * . The parsing according to the rules specified here is done in
 * {@link GroupingParser}.
 * </p>
 * <p>
 * Loading groupings is restricted to the following assumptions:
 * </p>
 * <ol>
 * <li>Groupings are based on an identifier-group relationship, where one group
 * is defined in a column in a delimited text-file.</li>
 * <li>The first column contains an identifier of the ID to be grouped.</li>
 * <li>Other columns contain a key specifying the group the ID belongs to. The
 * key can be an arbitrary string (except for the delimiter).</li>
 * <li>If the keys for two IDs is identical (according to
 * {@link String#equals(Object)}), they belong to the same group</li>
 * <li>The first row contains a header and is not considered a group or ID</li>
 * <li>The file contains at least one line with groupings</li>
 * </ol>
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
public class GroupingParseSpecification {
	
	
	/** The path to the file of the grouping. Mandatory. */
	private String path;

	/**
	 * Optional parameter to specify the column of the grouping in the delimited
	 * file. If no value is set, all columns are loaded.
	 */
	private ArrayList<Integer> columns;

	/**
	 * The text delimiter used. Default is tab ("\t");
	 */
	private String delimiter = "\t";

	
	/**
	 * Default Constructor, use setters to specify data
	 */
	public GroupingParseSpecification() {
	}
	
	/**
	 * Constructor with {@link #path} as argument.
	 */
	public GroupingParseSpecification(String path) {
		this.path = path;
	}
	
	/**
	 * @param path
	 *            setter, see {@link #path}
	 */
	public void setPath(String path) {
		this.path = path;
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
	 * @param delimiter
	 *            setter, see {@link #delimiter}
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the path, see {@link #path}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the delimiter, see {@link #delimiter}
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @return the column, see {@link #column}
	 */
	public ArrayList<Integer> getColumns() {
		return columns;
	}

}
