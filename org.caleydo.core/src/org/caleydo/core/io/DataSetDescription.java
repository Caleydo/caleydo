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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.io.parser.ascii.TabularDataParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;

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
	 * should be parsed one {@link ColumnDescription} object in ascending order
	 * of columns must be added.
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
	private ArrayList<ColumnDescription> parsingPattern = null;

	/**
	 * Flag determining whether the input matrix should be transposed, i.e.,
	 * whether the column in the source file should be the dimension (false) or
	 * the record (true). Defaults to false.
	 */
	private boolean transposeMatrix = false;

	/**
	 * Set whether the data you want to load is homogeneous, i.e. all the
	 * columns in the file are of the same semantic data type, i.e. they have
	 * the same value ranges, etc. If this is true the data scale used is the
	 * same for all columns. If this is false each column has its own data
	 * scale. Defaults to true.
	 */
	private boolean isDataHomogeneous = true;

	/**
	 * <p>
	 * Value that, if set, determines a neutral center point of the data, A
	 * common example is that 0 is the neutral value, lower values are in the
	 * negative and larger values are in the positive range. If this value is
	 * set it is be assumed that the extend into both, positive and negative
	 * direction is the same. Eg, for a dataset [-0.5, 0.7] with a center set at
	 * 0, the value range will be set to -0.7 to 0.7.
	 * </p>
	 * <p>
	 * Defaults to 0, i.e., data is not centered.
	 * </p>
	 */
	private Double dataCenter = null;

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
	 * {@link EDataTransformation}.
	 */
	private String mathFilterMode = "None";

	/**
	 * A list of path to grouping files for the columns of the file specified in
	 * {@link #dataSourcePath}. Optional.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications;

	/** Same as {@link #columnGroupingSpecifications} for rows. Optional. */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications;

	/**
	 * A description on how to pre-process (e.g., cluster, filter) the data.
	 * Optional.
	 */
	private DataProcessingDescription dataProcessingDescription;

	/** The color used to encode this data domain */
	private Color color;

	/**
	 * 
	 */
	public DataSetDescription() {
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
	 * @param dataCenter
	 *            setter, see {@link #dataCenter}
	 */
	public void setDataCenter(Double dataCenter) {
		this.dataCenter = dataCenter;
	}

	/**
	 * @return the dataCenter, see {@link #dataCenter}
	 */
	public Double getDataCenter() {
		return dataCenter;
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
	public void setParsingPattern(ArrayList<ColumnDescription> parsingPattern) {
		this.parsingPattern = parsingPattern;
	}

	public ArrayList<ColumnDescription> getParsingPattern() {

		if (parsingPattern != null && !(parsingPattern.size() == 0))
			return parsingPattern;

		if (parsingRules == null)
			return null;

		parsingPattern = new ArrayList<ColumnDescription>();

		Collections.sort(parsingRules);

		int numberOfColumns = 0;

		try {

			BufferedReader reader = GeneralManager.get().getResourceLoader()
					.getResource(dataSourcePath);

			// move the reader to the first line that contains the actual data
			for (int countHeaderLines = 0; countHeaderLines < numberOfHeaderLines; countHeaderLines++) {
				reader.readLine();
			}

			String dataLine = reader.readLine();
			String[] columns = dataLine.split(delimiter);
			numberOfColumns = columns.length;

		} catch (IOException e) {
			// Logger.log(new Status(Status.ERROR, "Parsing",
			// "Cannot read from: "
			// + dataSourcePath));
			// System.out.println("Cannot read from: " + dataSourcePath);
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
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule
						.getColumnDescripton().getDataType(), currentParsingRule
						.getColumnDescripton().getColumnType()));
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}
			if (columnCount < currentParsingRule.getToColumn()
					|| currentParsingRule.isParseUntilEnd()) {
				// we write the data type between the from and to column, or
				// between the from and end
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule
						.getColumnDescripton().getDataType(), currentParsingRule
						.getColumnDescripton().getColumnType()));
				continue;
			}
			if (columnCount == currentParsingRule.getToColumn()) {
				// we reach the end of a parsing rule
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule
						.getColumnDescripton().getDataType(), currentParsingRule
						.getColumnDescripton().getColumnType()));
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}

		}
		if (parsingPattern.size() == 0) {
			throw new IllegalStateException(
					"Failed to create parsing pattern based on the parsing rule / input file / header line information.");
		}
		return parsingPattern;
	}

	/**
	 * @param dataProcessingDescription
	 *            setter, see {@link #dataProcessingDescription}
	 */
	public void setDataProcessingDescription(
			DataProcessingDescription dataProcessingDescription) {
		this.dataProcessingDescription = dataProcessingDescription;
	}

	/**
	 * @return the dataProcessingDescriptions, see
	 *         {@link #dataProcessingDescriptions}
	 */
	public DataProcessingDescription getDataProcessingDescription() {
		return dataProcessingDescription;
	}

	/**
	 * Check whether all columns in the dataset to be loaded are continuous and
	 * numerical.
	 */
	public boolean areAllColumnTypesContinuous() {
		for (ColumnDescription columnDescription : getParsingPattern()) {
			if (!columnDescription.getColumnType().equalsIgnoreCase(
					ColumnDescription.CONTINUOUS))
				return false;
		}
		return true;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            setter, see {@link #color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		if (dataSetName != null)
			return dataSetName;
		else
			return dataSourcePath;
	}
}
