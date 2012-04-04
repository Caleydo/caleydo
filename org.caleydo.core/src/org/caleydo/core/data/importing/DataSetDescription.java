package org.caleydo.core.data.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.parser.ascii.GroupingParseSpecification;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * Description of a data set for creating Caleydo projects 
 * 
 * @author Alexander Lex
 * @author Nils Gehlenborg
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetDescription {

	/** The path to the source table csv file. Mandatory */
	private String dataSourcePath;

	/** The number of lines which should be ignored for parsing. */
	private Integer numberOfHeaderLines = 0;

	/**
	 * The text delimiter used. Default is tab ("\t");
	 */
	private String delimiter = "\t";

	/**
	 * Set {@link ParsingRule}s for the source file. Multiple ParsingRules are
	 * legal, where columns may be omitted, but no column may be added more than
	 * once!
	 */
	private ArrayList<ParsingRule> parsingRules;

	/**
	 * Flag determining whether the input matrix should be transposed, i.e.,
	 * whether the column in the source file should be the dimension (false) or
	 * the record (true). Defaults to false.
	 */
	private boolean transposeMatrix = false;
	/**
	 * A list of path to grouping files for the columns of the file specified in
	 * {@link #dataSourcePath}. Optional.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications;
	/** Same as {@link #columnGroupingSpecifications} for rows. Optional. */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications;

	/** A human readable name of the dataset. Optional. */
	private String dataSetName;

	/**
	 * <p>
	 * The name of the data type of the dimensions. For example, if the
	 * dimensions contain samples this should be <i>sample</i>.
	 * </p>
	 * <p>
	 * Based on this the ID mapping is created. The ID mapping assumes that in
	 * the line above the first record, labels identifying the dimensions are
	 * available.
	 * </p>
	 * <p>
	 * This means, that if you have two datasets that are cross-referenced (i.e.
	 * use the same type of IDs for their entries) the string specified here
	 * <b>must be identical</b> for both datasets. For example, if you have two
	 * datasets with samples as dimensions, you must in both cases use the
	 * string <i>sample</i> so that they can be resolved.
	 * </p>
	 * <p>
	 * The {@link IDCategory}, {@link IDType} and the denominations are created
	 * based on this.
	 * </p>
	 * <p>
	 * This is optional
	 * </p>
	 * This is only necessary if the {@link #dataDomainType} is not
	 * {@link GeneticDataDomain#DATA_DOMAIN_TYPE}
	 * <p>
	 */
	private String columnName;

	/**
	 * <p>
	 * Flag determining whether the column data type is for genes. If so, this
	 * must be specified. Defaults to false.
	 * </p>
	 * <p>
	 * If this is true the {@link #columnName} has no effect and needs not be
	 * set.
	 * </p>
	 */
	private boolean isColumnDataTypeGene = false;

	/**
	 * Same as {@link #columnName} but for rows.
	 */
	private String rowName;

	/**
	 * <p>
	 * Same as {@link #isColumnDataTypeGene} but for rows.
	 * </p>
	 * <p>
	 * If this is true the {@link #rowName} has no effect and needs not be set.
	 * </p>
	 */
	private boolean isRowDataTypeGene = false;

	/**
	 * @param dataSourcePath
	 *            setter, see {@link #dataSourcePath}
	 */
	public void setDataSourcePath(String dataSourcePath) {
		this.dataSourcePath = dataSourcePath;
	}

	/**
	 * @return the dataSourcePath, see {@link #dataSourcePath}
	 */
	public String getDataSourcePath() {
		return dataSourcePath;
	}

	/**
	 * @param numberOfHeaderLines
	 *            setter, see {@link #numberOfHeaderLines}
	 */
	public void setNumberOfHeaderLines(Integer numberOfHeaderLines) {
		this.numberOfHeaderLines = numberOfHeaderLines;
	}

	/**
	 * @return the numberOfHeaderLines, see {@link #numberOfHeaderLines}
	 */
	public Integer getNumberOfHeaderLines() {
		return numberOfHeaderLines;
	}

	/**
	 * @param delimiter
	 *            setter, see {@link #delimiter}
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the delimiter, see {@link #delimiter}
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param parsingRules
	 *            setter, see {@link #parsingRules}
	 */
	public void setParsingRules(ArrayList<ParsingRule> parsingRules) {
		this.parsingRules = parsingRules;
	}

	/**
	 * Adds a parsingRule to {@link #parsingRules}
	 * 
	 * @param parsingRule
	 */
	public void addParsingRule(ParsingRule parsingRule) {
		if (parsingRules == null)
			parsingRules = new ArrayList<ParsingRule>();

		parsingRules.add(parsingRule);
	}

	/**
	 * @return the parsingRules, see {@link #parsingRules}
	 */
	public ArrayList<ParsingRule> getParsingRules() {
		return parsingRules;
	}

	/**
	 * @param transposeMatrix
	 *            setter, see {@link #transposeMatrix}
	 */
	public void setTransposeMatrix(boolean transposeMatrix) {
		this.transposeMatrix = transposeMatrix;
	}

	/**
	 * @return the transposeMatrix, see {@link #transposeMatrix}
	 */
	public boolean isTransposeMatrix() {
		return transposeMatrix;
	}

	/**
	 * @param dataSetName
	 *            setter, see {@link #dataSetName}
	 */
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	/**
	 * @return the dataSetName, see {@link #dataSetName}
	 */
	public String getDataSetName() {
		return dataSetName;
	}

	/**
	 * @param columnName
	 *            setter, see {@link #columnName}
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return the columnName, see {@link #columnName}
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param isColumnDataTypeGene
	 *            setter, see {@link #isColumnDataTypeGene}
	 */
	public void setColumnDataTypeGene(boolean isColumnDataTypeGene) {
		this.isColumnDataTypeGene = isColumnDataTypeGene;
	}

	/**
	 * @return the isColumnDataTypeGene, see {@link #isColumnDataTypeGene}
	 */
	public boolean isColumnDataTypeGene() {
		return isColumnDataTypeGene;
	}

	/**
	 * @param rowName
	 *            setter, see {@link #rowName}
	 */
	public void setRowName(String rowName) {
		this.rowName = rowName;
	}

	/**
	 * @return the rowName, see {@link #rowName}
	 */
	public String getRowName() {
		return rowName;
	}

	/**
	 * @param isRowDataTypeGene
	 *            setter, see {@link #isRowDataTypeGene}
	 */
	public void setRowDataTypeGene(boolean isRowDataTypeGene) {
		this.isRowDataTypeGene = isRowDataTypeGene;
	}

	/**
	 * @return the isRowDataTypeGene, see {@link #isRowDataTypeGene}
	 */
	public boolean isRowDataTypeGene() {
		return isRowDataTypeGene;
	}

	/**
	 * Setter for {@link #columnGroupingSpecifications}. Overrides previous
	 * values of columnGroupingPaths
	 * 
	 * @param columnGroupingPaths
	 *            setter, see {@link #columnGroupingSpecifications}
	 */
	public void setColumnGroupingSpecifications(
			ArrayList<GroupingParseSpecification> columnGroupingSpecifications) {
		this.columnGroupingSpecifications = columnGroupingSpecifications;
	}

	/**
	 * Adds a path to the {@link #columnGroupingSpecifications}
	 * 
	 * @param columnGroupingSpecification
	 */
	public void addColumnGroupingSpecification(
			GroupingParseSpecification columnGroupingSpecification) {
		if (columnGroupingSpecifications == null) {
			columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
		}
		columnGroupingSpecifications.add(columnGroupingSpecification);
	}

	/**
	 * @return the columnGroupingSpecifications, see
	 *         {@link #columnGroupingSpecifications}
	 */
	public ArrayList<GroupingParseSpecification> getColumnGroupingSpecifications() {
		return columnGroupingSpecifications;
	}

	/**
	 * @param rowGroupingSpecifications
	 *            setter, see {@link #rowGroupingSpecifications}
	 */
	public void setRowGroupingSpecifications(
			ArrayList<GroupingParseSpecification> rowGroupingSpecifications) {
		this.rowGroupingSpecifications = rowGroupingSpecifications;
	}

	/**
	 * Adds a path to the {@link #rowGroupingSpecifications}
	 * 
	 * @param rowGroupingPath
	 *            a new path to the row groupings
	 */
	public void addRowGroupingSpecification(
			GroupingParseSpecification rowGroupingSpecification) {
		if (rowGroupingSpecifications == null) {
			rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
		}
		rowGroupingSpecifications.add(rowGroupingSpecification);
	}

	/**
	 * @return the rowGroupingSpecifications, see
	 *         {@link #rowGroupingSpecifications}
	 */
	public ArrayList<GroupingParseSpecification> getRowGroupingSpecifications() {
		return rowGroupingSpecifications;
	}

	public String getParsingPattern() {
		Collections.sort(parsingRules);

		int numberOfColumns = 0;
		String parsingPattern = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataSourcePath));
			// move the reader to the first line that contains the actual data
			for (int countHeaderLines = 0; countHeaderLines < numberOfHeaderLines; countHeaderLines++) {
				reader.readLine();
			}

			String dataLine = reader.readLine();
			String[] columns = dataLine.split(delimiter);
			numberOfColumns = columns.length;

		} catch (IOException e) {
			Logger.log(new Status(Status.ERROR, "Parsing", "Cannot read from: "
					+ dataSourcePath));
			throw new IllegalStateException("Cannot read from: " + dataSourcePath);
		}

		ParsingRule currentParsingRule = null;
		ParsingRule previousParsingRule = null;
		Iterator<ParsingRule> parsingRuleIterator = parsingRules.iterator();
		for (int columnCount = 0; columnCount < numberOfColumns; columnCount++) {
			if (currentParsingRule == null) {
				if (parsingRuleIterator.hasNext()) {
					currentParsingRule = parsingRuleIterator.next();

					// check validity of parsing rule
					if (currentParsingRule.getFromColumn() < 0
							|| currentParsingRule.getToColumn() > numberOfColumns
							|| (currentParsingRule.getToColumn() >= 0 && currentParsingRule
									.isParseUntilEnd())) {
						throw new IllegalStateException("Illegal Parsing Rule for File "
								+ dataSourcePath + "':\n " + currentParsingRule);
					}
					if (previousParsingRule != null) {
						if (previousParsingRule.getToColumn() >= currentParsingRule
								.getFromColumn()) {
							throw new IllegalStateException(
									"Parsingrules contain overlapping columns Rule 1:\n"
											+ previousParsingRule + "Rule 2:\n"
											+ currentParsingRule);
						}

					}
				} else {
					// we have passed the last rule and we fill the rest of the
					// columns up with skip
					parsingPattern += "SKIP;";
					continue;
				}
			}

			if (columnCount < currentParsingRule.getFromColumn()) {
				// we skip until we reach the from column
				parsingPattern += "SKIP;";
				continue;
			}
			if (currentParsingRule.getToColumn() < 0
					&& !currentParsingRule.isParseUntilEnd()) {
				// if only a single from column is specified we write that and
				// continue with the next parsing rule
				parsingPattern += currentParsingRule.getDataType() + ";";
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}
			if (columnCount < currentParsingRule.getToColumn()
					|| currentParsingRule.isParseUntilEnd()) {
				// we write the data type between the from and to column, or
				// between the from and end
				parsingPattern += currentParsingRule.getDataType() + ";";
				continue;
			}
			if (columnCount == currentParsingRule.getToColumn()) {
				// we reach the end of a parsing rule
				parsingPattern += currentParsingRule.getDataType() + ";";
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}

		}
		return parsingPattern;
	}
}
