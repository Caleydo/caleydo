/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IContainer;
import org.caleydo.core.data.collection.column.container.IntContainer;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Loader for tabular, matrix data from delimited text files.
 * </p>
 * <p>
 * Asides from loading the data from a file as specified in a supplied {@link DataSetDescription} the IDs of columns and
 * rows are parsed and dynamic IDs for columns and rows are created.
 * <p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TabularDataParser extends ATextParser {

	/**
	 * Imports data from file to this table. uses first dimension and overwrites first selection.
	 */
	protected ArrayList<IContainer<?>> targetColumns;

	/** The {@link ATableBasedDataDomain} for which the file is loaded */
	private ATableBasedDataDomain dataDomain;

	/** The {@link DataSetDescription} on which the loading of the file is based */
	private DataSetDescription dataSetDescription;

	/**
	 * Constructor.
	 */
	public TabularDataParser(ATableBasedDataDomain dataDomain, DataSetDescription dataSetDescription) {
		super(dataSetDescription.getDataSourcePath());

		this.dataDomain = dataDomain;
		this.dataSetDescription = dataSetDescription;
		targetColumns = new ArrayList<>();
	}

	/**
	 * <p>
	 * Creates the {@link Table} and the {@link AColumn}s for the {@link Table}, as well as the raw data columns to be
	 * set into the columns, which are also stored in {@link #targetColumns}.
	 * </p>
	 * <p>
	 * Also creates the mapping of columnIDs to column labels in the {@link IDMappingManager}
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	private void initializeTablePerspectives() {

		Table table = dataDomain.getTable();

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();

		String[] headers = null;
		if (dataSetDescription.isContainsColumnIDs()) {
			try {

				BufferedReader reader = GeneralManager.get().getResourceLoader().getResource(filePath);

				Integer rowOfColumnIDs = dataSetDescription.getNumberOfHeaderLines() - 1;
				if (dataSetDescription.getRowOfColumnIDs() != null)
					rowOfColumnIDs = dataSetDescription.getRowOfColumnIDs();

				for (int rowCount = 0; rowCount < rowOfColumnIDs; rowCount++) {
					reader.readLine();
				}
				String headerLine = reader.readLine();
				headers = headerLine.split(dataSetDescription.getDelimiter());
				reader.close();
			} catch (Exception e) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Could not read data file.", e));
				throw new IllegalStateException("Could not read data file '" + filePath + "'", e);
			}
		}

		calculateNumberOfLinesInFile();
		int numberOfDataLines = numberOfLinesInFile - dataSetDescription.getNumberOfHeaderLines();

		// prepare for id setting of column IDs
		IDMappingManager columnIDMappingManager;
		IDType targetColumnIDType;
		IDType sourceColumnIDType = IDType.getIDType(dataSetDescription.getColumnIDSpecification().getIdType());

		IDTypeParsingRules parsingRules = null;
		if (dataSetDescription.getColumnIDSpecification().getIdTypeParsingRules() != null)
			parsingRules = dataSetDescription.getColumnIDSpecification().getIdTypeParsingRules();
		else if (sourceColumnIDType.getIdTypeParsingRules() != null)
			parsingRules = sourceColumnIDType.getIdTypeParsingRules();

		if (!dataDomain.getDataSetDescription().isTransposeMatrix()) {
			columnIDMappingManager = dataDomain.getDimensionIDMappingManager();
			targetColumnIDType = dataDomain.getDimensionIDType();
		} else {
			columnIDMappingManager = dataDomain.getRecordIDMappingManager();
			targetColumnIDType = dataDomain.getRecordIDType();
		}

		MappingType mappingType = columnIDMappingManager.createMap(targetColumnIDType, sourceColumnIDType, false, true);
		// Map<Integer, String> columnIDMap =
		// columnIDMappingManager.getMap(mappingType);
		int columnID;

		for (ColumnDescription parsingDetail : parsingPattern) {
			switch (parsingDetail.getDataClass()) {
			case REAL_NUMBER: {
				FloatContainer container = new FloatContainer(numberOfDataLines);
				targetColumns.add(container);
				NumericalColumn<FloatContainer, Float> column;

				column = new NumericalColumn<>();

				column.setRawData(container);

				columnID = table.addColumn(column);
				break;
			}
			case NATURAL_NUMBER: {
				IntContainer container = new IntContainer(numberOfDataLines);
				targetColumns.add(container);
				NumericalColumn<IntContainer, Integer> column;

				column = new NumericalColumn<>();

				column.setRawData(container);

				columnID = table.addColumn(column);
				break;
			}
			case CATEGORICAL:
				switch (parsingDetail.getDataType()) {
				case STRING:
					CategoricalColumn<String> categoricalColumn;
					CategoricalContainer<String> categoricalContainer;
					categoricalContainer = new CategoricalContainer<String>(numberOfDataLines, EDataType.STRING);
					targetColumns.add(categoricalContainer);

					categoricalColumn = new CategoricalColumn<String>();
					categoricalColumn.setRawData(categoricalContainer);

					if (table instanceof CategoricalTable<?>) {
						categoricalColumn.setCategoryDescriptions(((CategoricalTable<String>) table)
								.getCategoryDescriptions());
					} else {
						// TODO support per column samples
						// categoricalColumn.setCategoryDescriptions(parsingDetail.getCategoricalDescription());
					}
					columnID = table.addColumn(categoricalColumn);
					break;
				case INTEGER:
					CategoricalColumn<Integer> categoricalIntColumn;
					CategoricalContainer<Integer> categoricalIntContainer;
					categoricalIntContainer = new CategoricalContainer<Integer>(numberOfDataLines, EDataType.INTEGER);
					targetColumns.add(categoricalIntContainer);

					categoricalIntColumn = new CategoricalColumn<Integer>();
					categoricalIntColumn.setRawData(categoricalIntContainer);

					if (table instanceof CategoricalTable<?>) {
						categoricalIntColumn.setCategoryDescriptions(((CategoricalTable<Integer>) table)
								.getCategoryDescriptions());
					}
					columnID = table.addColumn(categoricalIntColumn);
					break;
				case FLOAT:
				default:
					throw new IllegalStateException("DataType " + parsingDetail.getDataType()
							+ " not supported for class " + parsingDetail.getDataClass());

				}

				break;
			case UNIQUE_OBJECT:
			default:
				throw new IllegalStateException("Unknown or unimplemented column data type: " + parsingDetail + " in "
						+ parsingPattern);

			}
			if (headers != null) {
				String idString = headers[parsingDetail.getColumn()];
				idString = convertID(idString, parsingRules);
				columnIDMappingManager.addMapping(mappingType, columnID, idString);
			} else {
				columnIDMappingManager.addMapping(mappingType, columnID, "Column " + columnID);
			}
		}
	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		initializeTablePerspectives();

		// Init progress bar
		swtGuiManager.setProgressBarText("Loading data for: " + dataSetDescription.getDataSetName());
		float progressBarFactor = 100f / numberOfLinesInFile;

		for (int countHeaderLines = 0; countHeaderLines < dataSetDescription.getNumberOfHeaderLines(); countHeaderLines++) {
			reader.readLine();
		}

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();

		int lineCounter = 0;
		String numberParsingErrorMessage = "Could not parse a number in file " + dataSetDescription.getDataSetName()
				+ " at path " + filePath + "\n at the following locations: \n";
		boolean parsingErrorOccured = false;

		// ------------- ID parsing stuff ------------------------------
		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		IDCategory rowIDCategory = IDCategory.getIDCategory(rowIDSpecification.getIdCategory());
		IDType fromIDType = IDType.getIDType(rowIDSpecification.getIdType());

		IDType toIDType;
		if (dataDomain.isColumnDimension())
			toIDType = dataDomain.getRecordIDType();
		else
			toIDType = dataDomain.getDimensionIDType();

		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(rowIDCategory);
		int columnOfRowIDs = dataSetDescription.getColumnOfRowIds();

		MappingType mappingType = rowIDMappingManager.createMap(fromIDType, toIDType, false, true);

		IDTypeParsingRules parsingRules = null;
		if (rowIDSpecification.getIdTypeParsingRules() != null)
			parsingRules = rowIDSpecification.getIdTypeParsingRules();
		else if (toIDType.getIdTypeParsingRules() != null)
			parsingRules = fromIDType.getIdTypeParsingRules();

		String line;
		while ((line = reader.readLine()) != null) {
			// && lineInFile <= stopParsingAtLine) {

			String splitLine[] = line.split(dataSetDescription.getDelimiter());

			// id mapping
			String id = splitLine[columnOfRowIDs];
			id = convertID(id, parsingRules);
			rowIDMappingManager.addMapping(mappingType, id, lineCounter - startParsingAtLine);

			for (int count = 0; count < parsingPattern.size(); count++) {

				ColumnDescription columnDescription = parsingPattern.get(count);
				String cellContent = splitLine[columnDescription.getColumn()];

				switch (columnDescription.getDataType())

				{
				case FLOAT:
					float floatValue;
					try {
						floatValue = Float.parseFloat(cellContent);
					} catch (NumberFormatException nfe) {
						parsingErrorOccured = true;
						numberParsingErrorMessage += "column " + (columnDescription.getColumn()) + ", line "
								+ (lineCounter + dataSetDescription.getNumberOfHeaderLines()) + ". Cell content was: "
								+ cellContent + "\n";

						floatValue = Float.NaN;
					}
					FloatContainer targetColumn = (FloatContainer) targetColumns.get(count);
					if (lineCounter < targetColumn.size()) {
						targetColumn.add(floatValue);
					} else {
						Logger.log(new Status(IStatus.ERROR, this.toString(), "Index out of bounds at line: "
								+ lineCounter + " for column " + count));
					}
					break;
				case INTEGER:
					Integer intValue;
					try {
						if (cellContent.trim().equalsIgnoreCase("NA"))
							intValue = Integer.MIN_VALUE;
						else
							intValue = Integer.parseInt(cellContent);
					} catch (NumberFormatException nfe) {
						parsingErrorOccured = true;
						numberParsingErrorMessage += "column " + (columnDescription.getColumn()) + ", line "
								+ (lineCounter + dataSetDescription.getNumberOfHeaderLines()) + ". Cell content was: "
								+ cellContent + "\n";

						intValue = Integer.MIN_VALUE;
					}
					@SuppressWarnings("unchecked")
					IContainer<Integer> targetIntColumn = (IContainer<Integer>) targetColumns.get(count);
					if (lineCounter < targetIntColumn.size()) {
						targetIntColumn.add(intValue);
					} else {
						Logger.log(new Status(IStatus.ERROR, this.toString(), "Index out of bounds at line: "
								+ lineCounter + " for column " + count));
					}
					break;
				case STRING:
					String stringValue = cellContent.trim();

					@SuppressWarnings("unchecked")
					IContainer<String> targetStringColumn = (IContainer<String>) targetColumns.get(count);
					if (lineCounter < targetStringColumn.size()) {
						targetStringColumn.add(stringValue);
					} else {
						Logger.log(new Status(IStatus.ERROR, this.toString(), "Index out of bounds at line: "
								+ lineCounter + " for column " + count));
					}
					break;
				default:
					throw new IllegalStateException("Unknown data type: " + columnDescription.getDataType());

				}
				// if (columnDescription.getDataClass().equals(EDataClass.REAL_NUMBER)) {
				// FloatContainer targetColumn = (FloatContainer) targetColumns.get(count);
				// float value;
				// try {
				// value = Float.parseFloat(cellContent);
				// } catch (NumberFormatException nfe) {
				// parsingErrorOccured = true;
				// numberParsingErrorMessage += "column " + (columnDescription.getColumn()) + ", line "
				// + (lineCounter + dataSetDescription.getNumberOfHeaderLines()) + ". Cell content was: "
				// + cellContent + "\n";
				//
				// value = Float.NaN;
				// }
				// if (lineCounter < targetColumn.size()) {
				// targetColumn.addValue(value);
				// } else {
				// System.out.println("Index out of bounds at line: " + lineCounter + " for column " + count);
				// }
				// } else if (columnDescription.getDataType().equals(EDataClass.NOMINAL)) {
				// @SuppressWarnings("unchecked")
				// CategoricalContainer<String> targetColumn = (CategoricalContainer<String>) targetColumns.get(count);
				// targetColumn.addValue(splitLine[columnDescription.getColumn()]);
				// }
			}
			if (lineCounter % 100 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
			}
			lineCounter++;
		}

		if (parsingErrorOccured) {
			Logger.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID, numberParsingErrorMessage));
		}
	}
}
