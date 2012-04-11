package org.caleydo.core.data.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.parser.ascii.TabularDataParser;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * The DataSetDescription class contains rules for loading a data matrix from a
 * delimited text file.
 * </p>
 * <p>
 * The class is intended to be serialized using JAXB. XML representations that
 * can be de-serialized into this class are a form of persistent
 * parameterization of data loading.
 * </p>
 * <p>
 * For a general description of the contract of the files that can be read see
 * the base class {@link MatrixDefinition}.
 * </p>
 * <p>
 * At a minimum, the following information needs to be provided:
 * </p>
 * <ul>
 * <li>The path to the text file</li>
 * <li>A parsing specification, i.e. which columns to parse</li>
 * </ul>
 * <p>
 * 
 * </p>
 * <p>
 * Optionally references to other text files containing groupings of the columns
 * and/or rows can be specified.
 * </p>
 * <p>
 * As explained in {@link MatrixDefinition}, it is recommended that column and
 * row IDs are present in the source files. The ID type of the rows respectively
 * columns can be specified ({@link #rowIDSpecification} and
 * {@link #columnIDSpecification}). Multi-dataset relationships and mappings are
 * based on the same definition of the id type. For more Information see
 * {@link IDSpecification}.
 * 
 * @author Alexander Lex
 * @author Nils Gehlenborg
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetDescription extends MatrixDefinition {

	/** A human readable name of the dataset. Optional. */
	private String dataSetName;

	/**
	 * <p>
	 * Set {@link ParsingRule}s for the source file. Multiple ParsingRules are
	 * legal, where columns may be omitted, but no column may be added more than
	 * once!
	 * </p>
	 * <p>
	 * Alternatively a {@link #parsingPattern} can be set.
	 * </p>
	 */
	private ArrayList<ParsingRule> parsingRules;

	/**
	 * <p>
	 * The parsing pattern for the {@link TabularDataParser}, specifying the
	 * order of how to treat values between delimiters. For every column that
	 * should be parsed one {@link ColumnParsingDetail} object in ascending
	 * order of columns must be added.
	 * </p>
	 * <p>
	 * This is an alternative to the {@link #parsingRules}, which are
	 * "shorthand" for the parsingPattern. A parsingPattern is created for the
	 * {@link #parsingRules} specified, as only the parsingPattern is used for
	 * the actual parsing.
	 * </p>
	 * <p>
	 * If both, parsingPattern and parsingRules are specified the parsingPattern
	 * is used.
	 * </p>
	 * <p>
	 * {@link #getParsingPattern()} either returns this parsingPattern, or
	 * creates one based on the {@link #parsingRules}.
	 */
	private ArrayList<ColumnParsingDetail> parsingPattern = null;

	/**
	 * Flag determining whether the input matrix should be transposed, i.e.,
	 * whether the column in the source file should be the dimension (false) or
	 * the record (true). Defaults to false.
	 */
	private boolean transposeMatrix = false;



	/**
	 * Set whether the data you want to load is homogeneous, i.e. all the
	 * columns in the file are of the same data type. If this is true the data
	 * scale used is the same for all columns. If this is false each column has
	 * its own data scale. Defaults to true.
	 */
	private boolean isDataHomogeneous = true;

	/**
	 * An artificial min value used for normalization in the {@link DataTable}.
	 * Defaults to null.
	 */
	private Float min = null;

	/**
	 * An artificial max value used for normalization in the {@link DataTable}.
	 * Defaults to null.
	 */
	private Float max = null;

	/**
	 * Determines whether and if so which transformation should be applied to
	 * the data (e.g. log2 transformation). This is mapped to values of
	 * {@link ExternalDataRepresentation}.
	 */
	private String mathFilterMode = "Normal";

	/**
	 * A list of path to grouping files for the columns of the file specified in
	 * {@link #dataSourcePath}. Optional.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications;

	/** Same as {@link #columnGroupingSpecifications} for rows. Optional. */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications;

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
	 * @param isDataHomogeneous
	 *            setter, see {@link #isDataHomogeneous}
	 */
	public void setDataHomogeneous(boolean isDataHomogeneous) {
		this.isDataHomogeneous = isDataHomogeneous;
	}

	/**
	 * @return the isDataHomogeneous, see {@link #isDataHomogeneous}
	 */
	public boolean isDataHomogeneous() {
		return isDataHomogeneous;
	}

	/**
	 * @param min
	 *            setter, see {@link #min}
	 */
	public void setMin(Float min) {
		this.min = min;
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public Float getMin() {
		return min;
	}

	/**
	 * @param max
	 *            setter, see {@link #max}
	 */
	public void setMax(Float max) {
		this.max = max;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public Float getMax() {
		return max;
	}

	/**
	 * @return the mathFilterMode, see {@link #mathFilterMode}
	 */
	public String getMathFilterMode() {
		return mathFilterMode;
	}

	/**
	 * @param mathFilterMode
	 *            setter, see {@link #mathFilterMode}
	 */
	public void setMathFilterMode(String mathFilterMode) {
		this.mathFilterMode = mathFilterMode;
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
	 * @param parsingPattern
	 *            setter, see {@link #parsingPattern}
	 */
	public void setParsingPattern(ArrayList<ColumnParsingDetail> parsingPattern) {
		this.parsingPattern = parsingPattern;
	}

	public ArrayList<ColumnParsingDetail> getParsingPattern() {

		if (parsingPattern != null)
			return parsingPattern;

		if (parsingRules == null)
			return null;

		parsingPattern = new ArrayList<ColumnParsingDetail>();

		Collections.sort(parsingRules);

		int numberOfColumns = 0;

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
					// we have passed the last rule
					break;
				}
			}

			if (columnCount < currentParsingRule.getFromColumn()) {
				// we skip until we reach the from column
				continue;
			}
			if (currentParsingRule.getToColumn() < 0
					&& !currentParsingRule.isParseUntilEnd()) {
				// if only a single from column is specified we write that and
				// continue with the next parsing rule
				parsingPattern.add(new ColumnParsingDetail(columnCount,
						currentParsingRule.getDataType()));
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}
			if (columnCount < currentParsingRule.getToColumn()
					|| currentParsingRule.isParseUntilEnd()) {
				// we write the data type between the from and to column, or
				// between the from and end
				parsingPattern.add(new ColumnParsingDetail(columnCount,
						currentParsingRule.getDataType()));
				continue;
			}
			if (columnCount == currentParsingRule.getToColumn()) {
				// we reach the end of a parsing rule
				parsingPattern.add(new ColumnParsingDetail(columnCount,
						currentParsingRule.getDataType()));
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}

		}
		return parsingPattern;
	}

	@Override
	public String toString() {
		if (dataSetName != null)
			return "Info for " + dataSetName;
		else
			return "Info for " + dataSourcePath;
	}
}
