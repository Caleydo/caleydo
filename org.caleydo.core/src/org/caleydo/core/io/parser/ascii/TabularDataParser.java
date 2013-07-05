/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.column.GenericColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.GenericContainer;
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
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

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
	protected ArrayList<IContainer<?>> targetRawContainer;

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
		targetRawContainer = new ArrayList<>();
	}

	/**
	 * <p>
	 * Creates the {@link Table} and the {@link AColumn}s for the {@link Table}, as well as the raw data columns to be
	 * set into the columns, which are also stored in {@link #targetRawContainer}.
	 * </p>
	 * <p>
	 * Also creates the mapping of columnIDs to column labels in the {@link IDMappingManager}
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	private void initializTables() {

		Table table = dataDomain.getTable();

		List<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();

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

		int columnID;
		// this is null for inhomogeneous talbes.
		DataDescription dataDescription = dataSetDescription.getDataDescription();

		for (ColumnDescription columnDescription : parsingPattern) {
			if (columnDescription.getDataDescription() != null) {
				dataDescription = columnDescription.getDataDescription();
			}
			switch (dataDescription.getDataClass()) {
			case REAL_NUMBER: {
				FloatContainer container = new FloatContainer(numberOfDataLines);
				targetRawContainer.add(container);
				NumericalColumn<FloatContainer, Float> column = new NumericalColumn<>(dataDescription);

				column.setRawData(container);

				columnID = table.addColumn(column);
				break;
			}
			case NATURAL_NUMBER: {
				IntContainer container = new IntContainer(numberOfDataLines);
				targetRawContainer.add(container);
				NumericalColumn<IntContainer, Integer> column;

				column = new NumericalColumn<>(dataDescription);

				column.setRawData(container);

				columnID = table.addColumn(column);
				break;
			}
			case CATEGORICAL:
				switch (dataDescription.getRawDataType()) {
				case STRING:
					CategoricalColumn<String> categoricalColumn;
					CategoricalContainer<String> categoricalContainer;
					categoricalContainer = new CategoricalContainer<String>(numberOfDataLines, EDataType.STRING,
							CategoricalContainer.UNKNOWN_CATEOGRY_STRING);
					targetRawContainer.add(categoricalContainer);

					categoricalColumn = new CategoricalColumn<String>(dataDescription);
					categoricalColumn.setRawData(categoricalContainer);

					if (table instanceof CategoricalTable<?>) {
						categoricalColumn.setCategoryDescriptions(((CategoricalTable<String>) table)
								.getCategoryDescriptions());
					} else if (dataDescription.getCategoricalClassDescription() != null) {
						categoricalColumn.setCategoryDescriptions((CategoricalClassDescription<String>) dataDescription
								.getCategoricalClassDescription());
					}
					columnID = table.addColumn(categoricalColumn);
					break;
				case INTEGER:
					CategoricalColumn<Integer> categoricalIntColumn;
					CategoricalContainer<Integer> categoricalIntContainer;
					categoricalIntContainer = new CategoricalContainer<Integer>(numberOfDataLines, EDataType.INTEGER,
							CategoricalContainer.UNKNOWN_CATEGORY_INT);
					targetRawContainer.add(categoricalIntContainer);

					categoricalIntColumn = new CategoricalColumn<Integer>(dataDescription);
					categoricalIntColumn.setRawData(categoricalIntContainer);

					if (table instanceof CategoricalTable<?>) {
						categoricalIntColumn.setCategoryDescriptions(((CategoricalTable<Integer>) table)
								.getCategoryDescriptions());
					} else if (dataDescription.getCategoricalClassDescription() != null) {
						categoricalIntColumn
								.setCategoryDescriptions((CategoricalClassDescription<Integer>) dataDescription
										.getCategoricalClassDescription());
					}
					columnID = table.addColumn(categoricalIntColumn);
					break;
				case FLOAT:
				default:
					throw new IllegalStateException("DataType " + dataDescription.getRawDataType()
							+ " not supported for class " + dataDescription.getDataClass());

				}

				break;
			case UNIQUE_OBJECT:
				GenericContainer<String> container = new GenericContainer<>(numberOfDataLines);
				targetRawContainer.add(container);
				GenericColumn<String> column = new GenericColumn<>(dataDescription);
				column.setRawData(container);
				columnID = table.addColumn(column);
				break;
			default:
				throw new IllegalStateException("Unknown or unimplemented column data type: " + columnDescription
						+ " in " + parsingPattern);

			}
			if (headers != null) {
				String idString = headers[columnDescription.getColumn()];
				idString = convertID(idString, parsingRules);
				columnIDMappingManager.addMapping(mappingType, columnID, idString);
			} else {
				columnIDMappingManager.addMapping(mappingType, columnID, "Column " + columnID);
			}
		}
	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		SubMonitor monitor = GeneralManager.get().createSubProgressMonitor();
		monitor.beginTask("Loading data for: " + dataSetDescription.getDataSetName(), calculateNumberOfLinesInFile());
		initializTables();

		// Init progress bar
		for (int countHeaderLines = 0; countHeaderLines < dataSetDescription.getNumberOfHeaderLines(); countHeaderLines++) {
			reader.readLine();
		}
		monitor.worked(dataSetDescription.getNumberOfHeaderLines());

		List<ColumnDescription> parsingPattern = dataSetDescription.getOrCreateParsingPattern();

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
		// this is null for inhomogeneous data
		DataDescription dataDescription = dataSetDescription.getDataDescription();
		while ((line = reader.readLine()) != null) {
			// && lineInFile <= stopParsingAtLine) {

			String splitLine[] = line.split(dataSetDescription.getDelimiter());

			// id mapping
			String id = splitLine[columnOfRowIDs];
			id = convertID(id, parsingRules);
			rowIDMappingManager.addMapping(mappingType, id, lineCounter);

			for (int count = 0; count < parsingPattern.size(); count++) {

				ColumnDescription columnDescription = parsingPattern.get(count);
				if (columnDescription.getDataDescription() != null) {
					dataDescription = columnDescription.getDataDescription();
				}
				String cellContent = splitLine[columnDescription.getColumn()];

				try {
					switch (dataDescription.getRawDataType())

					{
					case FLOAT:
						float floatValue;
						FloatContainer targetColumn = (FloatContainer) targetRawContainer.get(count);
						try {
							floatValue = Float.parseFloat(cellContent);
							targetColumn.add(floatValue);
						} catch (NumberFormatException nfe) {
							parsingErrorOccured = true;
							numberParsingErrorMessage += "column " + (columnDescription.getColumn()) + ", line "
									+ (lineCounter + dataSetDescription.getNumberOfHeaderLines())
									+ ". Cell content was: " + cellContent + "\n";
							targetColumn.addUnknown();
						}

						break;
					case INTEGER:
						Integer intValue;
						@SuppressWarnings("unchecked")
						IContainer<Integer> targetIntColumn = (IContainer<Integer>) targetRawContainer.get(count);
						try {
							intValue = Integer.parseInt(cellContent);
							targetIntColumn.add(intValue);

						} catch (NumberFormatException nfe) {
							parsingErrorOccured = true;
							numberParsingErrorMessage += "column " + (columnDescription.getColumn()) + ", line "
									+ (lineCounter + dataSetDescription.getNumberOfHeaderLines())
									+ ". Cell content was: " + cellContent + "\n";
							targetIntColumn.addUnknown();
						}

						break;
					case STRING:
						String stringValue = cellContent.trim();

						@SuppressWarnings("unchecked")
						IContainer<String> targetStringColumn = (IContainer<String>) targetRawContainer.get(count);
						if (stringValue.length() == 0) {
							targetStringColumn.addUnknown();
							parsingErrorOccured = true;
						} else {
							targetStringColumn.add(stringValue);
						}
						break;
					default:
						throw new IllegalStateException("Unknown data type: " + dataDescription.getRawDataType());

					}
				} catch (IndexOutOfBoundsException ioobe) {
					// TODO this may be a little lenient - we should be stricter.
					Logger.log(new Status(IStatus.ERROR, this.toString(), "Index out of bounds at line: " + lineCounter
							+ " for column " + count, ioobe));
				}

			}
			if (lineCounter % 100 == 0) {
				monitor.worked(100);
			}
			lineCounter++;
		}

		if (parsingErrorOccured) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), numberParsingErrorMessage));
		}
		monitor.done();
	}

	@Override
	public String toString() {
		return "TabularDataParser: " + dataSetDescription.getDataSetName();
	}
}
