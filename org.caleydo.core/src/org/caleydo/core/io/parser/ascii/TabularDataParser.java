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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loader for tabular data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TabularDataParser extends ATextParser {

	/**
	 * Imports data from file to this table. uses first dimension and overwrites
	 * first selection.
	 */
	protected ArrayList<Object> targetColumns;

	private ATableBasedDataDomain dataDomain;

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
	private void initializeDataContainers() {

		DataTable table = new DataTable(dataDomain);
		dataDomain.setTable(table);

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription
				.getParsingPattern();

		String[] headers = null;
		if (dataSetDescription.isContainsColumnIDs()) {
			try {

				BufferedReader reader = new BufferedReader(new FileReader(fileName));

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
				throw new IllegalStateException("Could not read data file '" + fileName
						+ "'", e);
			}
		}

		calculateNumberOfLinesInFile();
		int numberOfDataLines = numberOfLinesInFile
				- dataSetDescription.getNumberOfHeaderLines();

		// prepare for id setting of column IDs
		IDMappingManager columnIDMappingManager;
		IDType columnIDType;
		IDType hrColumnIDType;
		if (!dataDomain.getDataSetDescription().isTransposeMatrix()) {
			columnIDMappingManager = dataDomain.getDimensionIDMappingManager();
			columnIDType = dataDomain.getDimensionIDType();
			hrColumnIDType = dataDomain.getHumanReadableDimensionIDType();
		} else {
			columnIDMappingManager = dataDomain.getRecordIDMappingManager();
			columnIDType = dataDomain.getRecordIDType();
			hrColumnIDType = dataDomain.getHumanReadableRecordIDType();
		}

		MappingType mappingType = columnIDMappingManager.createMap(columnIDType,
				hrColumnIDType, false);
		Map<Integer, String> columnIDMap = columnIDMappingManager.getMap(mappingType);

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
				idString = convertID(idString,
						dataSetDescription.getColumnIDSpecification());
				columnIDMap.put(columnID, idString);
			} else {
				columnIDMap.put(columnID, "Column " + columnCount++);
			}

		}

		columnIDMappingManager.createReverseMap(mappingType);

	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		initializeDataContainers();

		// Init progress bar
		swtGuiManager.setProgressBarText("Load data file " + fileName);

		String line;

		float progressBarFactor = 100f / numberOfLinesInFile;

		for (int countHeaderLines = 0; countHeaderLines < dataSetDescription
				.getNumberOfHeaderLines(); countHeaderLines++) {
			reader.readLine();
		}

		ArrayList<ColumnDescription> parsingPattern = dataSetDescription
				.getParsingPattern();

		int lineCounter = 0;
		while ((line = reader.readLine()) != null) {
			// && lineInFile <= stopParsingAtLine) {

			String splitLine[] = line.split(dataSetDescription.getDelimiter());

			for (int count = 0; count < parsingPattern.size(); count++) {
				ColumnDescription column = parsingPattern.get(count);

				String cellContent = splitLine[column.getColumn()];
				if (column.getDataType().equalsIgnoreCase("float")) {
					float[] targetColumn = (float[]) targetColumns.get(count);
					Float value;
					try {
						value = Float.parseFloat(cellContent);
					} catch (NumberFormatException nfe) {

						String errorMessage = "Could not parse a number. \""
								+ cellContent
								+ "\" at ["
								+ (column.getColumn())
								+ ", "
								+ (lineCounter + dataSetDescription
										.getNumberOfHeaderLines()) + "]. Assigning NaN";
						Logger.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID,
								errorMessage));
						value = Float.NaN;
					}
					targetColumn[lineCounter] = value;
				} else if (column.getDataType().equalsIgnoreCase("string")) {
					@SuppressWarnings("unchecked")
					ArrayList<String> targetColumn = (ArrayList<String>) targetColumns
							.get(count);
					targetColumn.add(splitLine[column.getColumn()]);
				}
				if (lineCounter % 100 == 0) {
					swtGuiManager
							.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
				}
			}
			lineCounter++;
		}

	}
	/**
	 * Method masking errors with import of categorical data FIXME: find the
	 * underlying error instead
	 * 
	 * @param rawStringData
	 * @return
	 */
	// private ArrayList<String> fillUp(ArrayList<String> rawStringData) {
	// int missingValues = nrLinesToRead - rawStringData.size();
	// if (missingValues > 0) {
	// for (int count = rawStringData.size(); count < nrLinesToRead; count++)
	// rawStringData.add("ARTIFICIAL");
	//
	// Logger.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID,
	// "Had to fill up stroarge with "
	// + missingValues + " artificial strings"));
	//
	// }
	// return rawStringData;
	//
	// }

}
