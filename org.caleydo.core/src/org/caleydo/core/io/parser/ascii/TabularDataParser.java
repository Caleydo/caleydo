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
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.data.collection.table.DataTable;
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
 * Asides from loading the data from a file as specified in a supplied
 * {@link DataSetDescription} the IDs of columns and rows are parsed and dynamic
 * IDs for columns and rows are created.
 * <p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TabularDataParser extends ATextParser {

	/**
	 * Imports data from file to this table. uses first dimension and overwrites
	 * first selection.
	 */
	protected ArrayList<Object> targetColumns;

	/** The {@link ATableBasedDataDomain} for which the file is loaded */
	private ATableBasedDataDomain dataDomain;

	/** The {@link DataSetDescription} on which the loading of the file is based */
	private DataSetDescription dataSetDescription;

	/**
	 * Constructor.
	 */
	public TabularDataParser(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {
		super(dataSetDescription.getDataSourcePath());

		this.dataDomain = dataDomain;
		this.dataSetDescription = dataSetDescription;
		targetColumns = new ArrayList<Object>();
	}

	/**
	 * <p>
	 * Creates the {@link DataTable} and the {@link AColumn}s for the
	 * {@link DataTable}, as well as the raw data columns to be set into the
	 * columns, which are also stored in {@link #targetColumns}.
	 * </p>
	 * <p>
	 * Also creates the mapping of columnIDs to column labels in the
	 * {@link IDMappingManager}
	 * </p>
	 */
	private void initializeTablePerspectives() {

		DataTable table = new DataTable(dataDomain);
		dataDomain.setTable(table);

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription
				.getParsingPattern();

		String[] headers = null;
		if (dataSetDescription.isContainsColumnIDs()) {
			try {

				BufferedReader reader = GeneralManager.get().getResourceLoader()
						.getResource(filePath);

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
				Logger.log(new Status(IStatus.ERROR, this.toString(),
						"Could not read data file.", e));
				throw new IllegalStateException("Could not read data file '" + filePath
						+ "'", e);
			}
		}

		calculateNumberOfLinesInFile();
		int numberOfDataLines = numberOfLinesInFile
				- dataSetDescription.getNumberOfHeaderLines();

		// prepare for id setting of column IDs
		IDMappingManager columnIDMappingManager;
		IDType targetColumnIDType;
		IDType sourceColumnIDType = IDType.getIDType(dataSetDescription
				.getColumnIDSpecification().getIdType());

		IDTypeParsingRules parsingRules = null;
		if (dataSetDescription.getColumnIDSpecification().getIdTypeParsingRules() != null)
			parsingRules = dataSetDescription.getColumnIDSpecification()
					.getIdTypeParsingRules();
		else if (sourceColumnIDType.getIdTypeParsingRules() != null)
			parsingRules = sourceColumnIDType.getIdTypeParsingRules();

		
		if (!dataDomain.getDataSetDescription().isTransposeMatrix()) {
			columnIDMappingManager = dataDomain.getDimensionIDMappingManager();
			targetColumnIDType = dataDomain.getDimensionIDType();
		} else {
			columnIDMappingManager = dataDomain.getRecordIDMappingManager();
			targetColumnIDType = dataDomain.getRecordIDType();
		}

		MappingType mappingType = columnIDMappingManager.createMap(targetColumnIDType,
				sourceColumnIDType, false, true);
		// Map<Integer, String> columnIDMap =
		// columnIDMappingManager.getMap(mappingType);

		int columnCount = 0;
		for (ColumnDescription parsingDetail : parsingPattern) {
			int columnID;
			if (parsingDetail.getDataType().equalsIgnoreCase("float")) {
				float[] dataColumn = new float[numberOfDataLines];
				targetColumns.add(dataColumn);
				NumericalColumn column;
				if (parsingDetail.getColumnID() == null) {
					column = new NumericalColumn();
					parsingDetail.setColumnID(column.getID());
				} else {
					column = new NumericalColumn(parsingDetail.getColumnID());
				}

				columnID = column.getID();
				column.setRawData(dataColumn);
				table.addColumn(column);
			} else if (parsingDetail.getDataType().equalsIgnoreCase("string")) {
				ArrayList<String> dataColumn = new ArrayList<String>(numberOfDataLines);
				targetColumns.add(dataColumn);
				NominalColumn<String> column;
				if (parsingDetail.getColumnID() == null) {
					column = new NominalColumn<String>();
					parsingDetail.setColumnID(column.getID());
				} else {
					column = new NominalColumn<String>(parsingDetail.getColumnID());

				}
				columnID = column.getID();
				column.setRawNominalData(dataColumn);
				table.addColumn(column);
			} else {
				throw new IllegalStateException("Unknown column data type: "
						+ parsingDetail + " in " + parsingPattern);
			}

			if (headers != null) {
				String idString = headers[parsingDetail.getColumn()];
				idString = convertID(idString, parsingRules);
				columnIDMappingManager.addMapping(mappingType, columnID, idString);
			} else {
				columnIDMappingManager.addMapping(mappingType, columnID, "Column "
						+ columnCount++);
			}

		}

	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		initializeTablePerspectives();

		// Init progress bar
		swtGuiManager.setProgressBarText("Loading data for: "
				+ dataSetDescription.getDataSetName());
		float progressBarFactor = 100f / numberOfLinesInFile;

		for (int countHeaderLines = 0; countHeaderLines < dataSetDescription
				.getNumberOfHeaderLines(); countHeaderLines++) {
			reader.readLine();
		}

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription
				.getParsingPattern();

		int lineCounter = 0;
		String numberParsingErrorMessage = "Could not parse a number in file "
				+ dataSetDescription.getDataSetName() + " at path " + filePath
				+ "\n at the following locations: \n";
		boolean parsingErrorOccured = false;

		// ------------- ID parsing stuff ------------------------------
		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		IDCategory rowIDCategory = IDCategory.getIDCategory(rowIDSpecification
				.getIdCategory());
		IDType fromIDType = IDType.getIDType(rowIDSpecification.getIdType());

		IDType toIDType;
		if (dataDomain.isColumnDimension())
			toIDType = dataDomain.getRecordIDType();
		else
			toIDType = dataDomain.getDimensionIDType();

		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(rowIDCategory);
		int columnOfRowIDs = dataSetDescription.getColumnOfRowIds();

		MappingType mappingType = rowIDMappingManager.createMap(fromIDType, toIDType,
				false, true);

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
			rowIDMappingManager.addMapping(mappingType, id, lineCounter
					- startParsingAtLine);

			for (int count = 0; count < parsingPattern.size(); count++) {
				ColumnDescription column = parsingPattern.get(count);

				String cellContent = splitLine[column.getColumn()];
				if (column.getDataType().equalsIgnoreCase("float")) {
					float[] targetColumn = (float[]) targetColumns.get(count);
					Float value;
					try {
						value = Float.parseFloat(cellContent);
					} catch (NumberFormatException nfe) {
						parsingErrorOccured = true;
						numberParsingErrorMessage += "column "
								+ (column.getColumn())
								+ ", line "
								+ (lineCounter + dataSetDescription
										.getNumberOfHeaderLines())
								+ ". Cell content was: " + cellContent + "\n";

						value = Float.NaN;
					}
					if (lineCounter < targetColumn.length) {
						targetColumn[lineCounter] = value;
					} else {
						System.out.println("Index out of bounds at line: " + lineCounter
								+ " for column " + count);
					}
				} else if (column.getDataType().equalsIgnoreCase("string")) {
					@SuppressWarnings("unchecked")
					ArrayList<String> targetColumn = (ArrayList<String>) targetColumns
							.get(count);
					targetColumn.add(splitLine[column.getColumn()]);
				}				
			}
			if (lineCounter % 100 == 0) {
				swtGuiManager
						.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
			}
			lineCounter++;
		}

		if (parsingErrorOccured) {
			Logger.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID,
					numberParsingErrorMessage));
		}
	}
}
