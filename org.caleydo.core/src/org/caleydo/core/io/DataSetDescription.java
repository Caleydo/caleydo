/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.parser.ascii.TabularDataParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;

/**
 * <p>
 * The DataSetDescription class contains rules for loading a data matrix from a delimited text file.
 * </p>
 * <p>
 * The class is intended to be serialized using JAXB. XML representations that can be de-serialized into this class are
 * a form of persistent parameterization of data loading.
 * </p>
 * <p>
 * For a general description of the contract of the files that can be read see the base class {@link MatrixDefinition}.
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
 * Optionally references to other text files containing groupings of the columns and/or rows can be specified.
 * </p>
 * <p>
 * As explained in {@link MatrixDefinition}, it is recommended that column and row IDs are present in the source files.
 * The ID type of the rows respectively columns can be specified ({@link #rowIDSpecification} and
 * {@link #columnIDSpecification}). Multi-dataset relationships and mappings are based on the same definition of the id
 * type. For more Information see {@link IDSpecification}.
 *
 * @author Alexander Lex
 * @author Nils Gehlenborg
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetDescription extends MatrixDefinition {

	/**
	 * Enum for signalling that either a default {@link NumericalProperties} or a default {@link CategoricalProperties}
	 * should be created.
	 */
	public enum ECreateDefaultProperties {
		/**
		 * Sets {@link EDataClass#REAL_NUMBER} {@link EDataType#FLOAT}
		 */
		NUMERICAL,
		/**
		 * Sets {@link EDataClass#CATEGORICAL} {@link EDataType#INTEGER}
		 */
		CATEGORICAL
	}

	/** A human readable name of the dataset. Optional. */
	private String dataSetName;

	private MetaDataElement metaData;

	/**
	 * <p>
	 * Set {@link ParsingRule}s for the source file. Multiple ParsingRules are legal, where columns may be omitted, but
	 * no column may be added more than once!
	 * </p>
	 * <p>
	 * Alternatively a {@link #parsingPattern} can be set.
	 * </p>
	 */
	private List<ParsingRule> parsingRules;

	/**
	 * <p>
	 * The parsing pattern for the {@link TabularDataParser}, specifying the order of how to treat values between
	 * delimiters. For every column that should be parsed one {@link ColumnDescription} object in ascending order of
	 * columns must be added.
	 * </p>
	 * <p>
	 * This is an alternative to the {@link #parsingRules}, which are "shorthand" for the parsingPattern. A
	 * parsingPattern is created for the {@link #parsingRules} specified, as only the parsingPattern is used for the
	 * actual parsing.
	 * </p>
	 * <p>
	 * If both, parsingPattern and parsingRules are specified the parsingPattern is used.
	 * </p>
	 * <p>
	 * {@link #getOrCreateParsingPattern()} either returns this parsingPattern, or creates one based on the
	 * {@link #parsingRules}.<
	 */
	@XmlElement
	private List<ColumnDescription> parsingPattern = null;

	/**
	 * Flag determining whether the input matrix should be transposed, i.e., whether the column in the source file
	 * should be the dimension (false) or the record (true). Defaults to false.
	 */
	private boolean transposeMatrix = false;

	/**
	 * A list of path to grouping files for the columns of the file specified in {@link #dataSourcePath}. Optional.
	 */
	private List<GroupingParseSpecification> columnGroupingSpecifications;

	/** Same as {@link #columnGroupingSpecifications} for rows. Optional. */
	private List<GroupingParseSpecification> rowGroupingSpecifications;

	/**
	 * A description on how to pre-process (e.g., cluster, filter) the data. Optional.
	 */
	private DataProcessingDescription dataProcessingDescription;

	/** The color used to encode this data domain */
	private Color color;

	/**
	 * Meta-data about the data used when dealing with homogeneous datasets. This must be set before adding columns in
	 * the homogeneous case.
	 */
	private DataDescription dataDescription = null;

	/**
	 *
	 */
	public DataSetDescription() {
	}

	/**
	 * Creates a {@link DataSetDescription} as a homogeneous dataset by setting {@link #dataDescription}. The parameter
	 * determines whether the {@link DataDescription} gets a default {@link #numericalProperties} or
	 * {@link #categoricalClassDescription} object.
	 *
	 * @param createDefaultProperties
	 */
	public DataSetDescription(ECreateDefaultProperties createDefaultProperties) {
		switch (createDefaultProperties) {
		case NUMERICAL:
			dataDescription = new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT, new NumericalProperties());
			break;
		case CATEGORICAL:
			dataDescription = new DataDescription(EDataClass.CATEGORICAL, EDataType.INTEGER,
					new CategoricalClassDescription<Integer>());
			break;

		default:
			throw new IllegalStateException("Unknown createDefaultProperty: " + createDefaultProperties);
		}

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
	 * Setter for {@link #columnGroupingSpecifications}. Overrides previous values of columnGroupingPaths
	 *
	 * @param columnGroupingPaths
	 *            setter, see {@link #columnGroupingSpecifications}
	 */
	public void setColumnGroupingSpecifications(List<GroupingParseSpecification> columnGroupingSpecifications) {
		this.columnGroupingSpecifications = columnGroupingSpecifications;
	}

	/**
	 * Adds a path to the {@link #columnGroupingSpecifications}
	 *
	 * @param columnGroupingSpecification
	 */
	public void addColumnGroupingSpecification(GroupingParseSpecification columnGroupingSpecification) {
		if (columnGroupingSpecifications == null) {
			columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
		}
		columnGroupingSpecifications.add(columnGroupingSpecification);
	}

	/**
	 * @return the columnGroupingSpecifications, see {@link #columnGroupingSpecifications}
	 */
	public List<GroupingParseSpecification> getColumnGroupingSpecifications() {
		return columnGroupingSpecifications;
	}

	/**
	 * @param rowGroupingSpecifications
	 *            setter, see {@link #rowGroupingSpecifications}
	 */
	public void setRowGroupingSpecifications(List<GroupingParseSpecification> rowGroupingSpecifications) {
		this.rowGroupingSpecifications = rowGroupingSpecifications;
	}

	/**
	 * Adds a path to the {@link #rowGroupingSpecifications}
	 *
	 * @param rowGroupingPath
	 *            a new path to the row groupings
	 */
	public void addRowGroupingSpecification(GroupingParseSpecification rowGroupingSpecification) {
		if (rowGroupingSpecifications == null) {
			rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
		}
		rowGroupingSpecifications.add(rowGroupingSpecification);
	}

	/**
	 * @return the rowGroupingSpecifications, see {@link #rowGroupingSpecifications}
	 */
	public List<GroupingParseSpecification> getRowGroupingSpecifications() {
		return rowGroupingSpecifications;
	}

	/**
	 * @param parsingRules
	 *            setter, see {@link #parsingRules}
	 */
	public void setParsingRules(List<ParsingRule> parsingRules) {
		for (ParsingRule rule : parsingRules) {
			checkAndDataDescritptionForColumn(rule.getColumnDescripton());
		}
		this.parsingRules = parsingRules;
	}

	/**
	 * Adds a parsingRule to {@link #parsingRules}
	 *
	 * @param parsingRule
	 */
	public void addParsingRule(ParsingRule parsingRule) {
		checkAndDataDescritptionForColumn(parsingRule.getColumnDescripton());
		if (parsingRules == null)
			parsingRules = new ArrayList<ParsingRule>();

		parsingRules.add(parsingRule);
	}

	/**
	 * @return the parsingRules, see {@link #parsingRules}
	 */
	public List<ParsingRule> getParsingRules() {
		return parsingRules;
	}

	/**
	 * @param parsingPattern
	 *            setter, see {@link #parsingPattern}
	 */
	public void setParsingPattern(List<ColumnDescription> parsingPattern) {
		for (ColumnDescription desc : parsingPattern) {
			checkAndDataDescritptionForColumn(desc);
		}
		this.parsingPattern = parsingPattern;
	}

	public void addParsingPattern(ColumnDescription desc) {
		checkAndDataDescritptionForColumn(desc);
		if (this.parsingPattern == null)
			this.parsingPattern = new ArrayList<>();
		this.parsingPattern.add(desc);
	}

	private void checkAndDataDescritptionForColumn(ColumnDescription desc) {
		if (desc.getDataDescription() == null && dataDescription != null) {
			// desc.setDataDescription(dataDescription);
		} else if (desc.getDataDescription() != null && dataDescription != null
				&& !(desc.getDataDescription().equals(dataDescription))) {
			throw new IllegalStateException("DataDescription was defined for both table and column in "
					+ this.toString());
		} else if (desc.getDataDescription() == null && dataDescription == null) {
			throw new IllegalStateException("DataDescription was not defined." + this.toString());
		}
	}

	public List<ColumnDescription> getOrCreateParsingPattern() {

		if (parsingPattern != null && !(parsingPattern.size() == 0))
			return parsingPattern;

		if (parsingRules == null)
			return null;

		parsingPattern = new ArrayList<ColumnDescription>();

		Collections.sort(parsingRules);

		int numberOfColumns = 0;

		try {

			BufferedReader reader = GeneralManager.get().getResourceLoader().getResource(dataSourcePath);

			// move the reader to the first line that contains the actual data
			for (int countHeaderLines = 0; countHeaderLines < numberOfHeaderLines; countHeaderLines++) {
				reader.readLine();
			}

			String dataLine = reader.readLine();
			reader.close();
			String[] columns = dataLine.split(delimiter, -1);
			numberOfColumns = columns.length;

		} catch (IOException e) {
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
					if (currentParsingRule.getFromColumn() < 0 || currentParsingRule.getToColumn() > numberOfColumns
							|| (currentParsingRule.getToColumn() >= 0 && currentParsingRule.isParseUntilEnd())) {
						throw new IllegalStateException("Illegal Parsing Rule for File " + dataSourcePath + "':\n "
								+ currentParsingRule);
					}
					if (previousParsingRule != null) {
						if (previousParsingRule.getToColumn() >= currentParsingRule.getFromColumn()) {
							throw new IllegalStateException("Parsingrules contain overlapping columns Rule 1:\n"
									+ previousParsingRule + "Rule 2:\n" + currentParsingRule);
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
			if (currentParsingRule.getToColumn() < 0 && !currentParsingRule.isParseUntilEnd()) {
				// if only a single from column is specified we write that and
				// continue with the next parsing rule
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule.getColumnDescripton()
						.getDataDescription()));
				previousParsingRule = currentParsingRule;
				currentParsingRule = null;
				continue;
			}

			if (columnCount < currentParsingRule.getToColumn() || currentParsingRule.isParseUntilEnd()) {
				// we write the data type between the from and to column, or
				// between the from and end
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule.getColumnDescripton()
						.getDataDescription()));
				continue;
			}
			if (columnCount == currentParsingRule.getToColumn()) {
				// we reach the end of a parsing rule
				parsingPattern.add(new ColumnDescription(columnCount, currentParsingRule.getColumnDescripton()
						.getDataDescription()));
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
	 * @param dataDescription
	 *            setter, see {@link dataDescription}
	 */
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
	}

	/**
	 * @return the dataDescription, see {@link #dataDescription}
	 */
	public DataDescription getDataDescription() {
		return dataDescription;
	}

	/**
	 * @param dataProcessingDescription
	 *            setter, see {@link #dataProcessingDescription}
	 */
	public void setDataProcessingDescription(DataProcessingDescription dataProcessingDescription) {
		this.dataProcessingDescription = dataProcessingDescription;
	}

	/**
	 * @return the dataProcessingDescriptions, see {@link #dataProcessingDescriptions}
	 */
	public DataProcessingDescription getDataProcessingDescription() {
		return dataProcessingDescription;
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
		String data = "";
		if (dataSetName != null)
			data += dataSetName;
		else
			data += dataSourcePath;

		if (dataDescription != null)
			return data + dataDescription.toString();

		return data + "(hybrid/inhomogeneous)";
	}

	/**
	 * @param metaData
	 *            setter, see {@link metaData}
	 */
	public void setMetaData(MetaDataElement metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the metaData, see {@link #metaData}
	 */
	public MetaDataElement getMetaData() {
		return metaData;
	}
}
