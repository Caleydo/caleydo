package org.caleydo.core.parser.ascii;

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
import org.caleydo.core.data.importing.DataSetDescription;
import org.caleydo.core.data.importing.ColumnParsingDetail;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
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

	private DataSetDescription dataSetDescription;

	// protected ArrayList<EColumnType> columnDataTypes;

	// private ArrayList<int[]> intArrays;
	//
	// private ArrayList<float[]> floatArrays;
	//
	// private ArrayList<ArrayList<String>> stringLists;

	// private boolean useExperimentClusterInfo;

	private ATableBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public TabularDataParser(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {
		super(dataSetDescription.getDataSourcePath());

		this.dataDomain = dataDomain;
		this.dataSetDescription = dataSetDescription;
		targetColumns = new ArrayList<Object>();

		// intArrays = new ArrayList<int[]>();
		// floatArrays = new ArrayList<float[]>();
		// stringLists = new ArrayList<ArrayList<String>>();

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

		ArrayList<ColumnParsingDetail> parsingPattern = dataSetDescription
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
		for (ColumnParsingDetail dataType : parsingPattern) {
			int columnID;
			if (dataType.getDataType().equalsIgnoreCase("float")) {
				float[] dataColumn = new float[numberOfDataLines];
				targetColumns.add(dataColumn);
				NumericalColumn column = new NumericalColumn();
				columnID = column.getID();
				column.setRawData(dataColumn);
				table.addColumn(column);
			} else if (dataType.getDataType().equalsIgnoreCase("string")) {
				ArrayList<String> dataColumn = new ArrayList<String>(numberOfDataLines);
				targetColumns.add(dataColumn);
				NominalColumn<String> column = new NominalColumn<String>();
				columnID = column.getID();
				column.setRawNominalData(dataColumn);
				table.addColumn(column);
			} else {
				throw new IllegalStateException("Unknown column data type: " + dataType
						+ " in " + parsingPattern);
			}

			if (headers != null) {
				String idString = headers[dataType.getColumn()];
				idString = convertID(idString,
						dataSetDescription.getColumnIDSpecification());
				columnIDMap.put(columnID, idString);
			} else {
				columnIDMap.put(columnID, "Column " + columnCount++);
			}

		}

		columnIDMappingManager.createReverseMap(mappingType);

		// while (tokenizer.hasMoreTokens()) {
		// String token = tokenizer.nextToken(delimiter);
		//
		// if (token.equalsIgnoreCase("abort")) {
		// columnDataTypes.add(EColumnType.ABORT);
		// return;
		// } else if (token.equalsIgnoreCase("skip")) {
		// columnDataTypes.add(EColumnType.SKIP);
		// } else if (token.equalsIgnoreCase("int")) {
		// columnDataTypes.add(EColumnType.INT);
		// } else if (token.equalsIgnoreCase("float")) {
		// columnDataTypes.add(EColumnType.FLOAT);
		// } else if (token.equalsIgnoreCase("string")) {
		// columnDataTypes.add(EColumnType.STRING);
		// } else if (token.equalsIgnoreCase("certainty")) {
		// columnDataTypes.add(EColumnType.CERTAINTY);
		// }
		//
		//

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

		// int max = stopParsingAtLine - parsingStartLine + 1;

		ArrayList<ColumnParsingDetail> parsingPattern = dataSetDescription
				.getParsingPattern();

		int lineCounter = 0;
		while ((line = reader.readLine()) != null) {
			// && lineInFile <= stopParsingAtLine) {

			String splitLine[] = line.split(dataSetDescription.getDelimiter());

			for (int count = 0; count < parsingPattern.size(); count++) {
				ColumnParsingDetail column = parsingPattern.get(count);

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

		// for (EColumnType columnDataType : columnDataTypes) {
		// if (strTokenLine.hasMoreTokens()) {
		// switch (columnDataType) {
		// case INT:
		// intArrays.get(intIndex)[lineInFile - parsingStartLine] = Integer
		// .valueOf(strTokenLine.nextToken()).intValue();
		// columnIndex++;
		// intIndex++;
		// break;
		// case FLOAT:
		// case CERTAINTY:
		// Float value;
		// tempToken = strTokenLine.nextToken();
		// try {
		// value = Float.parseFloat(tempToken);
		// } catch (NumberFormatException nfe) {
		//
		// String sErrorMessage = "Could not parse a number. \""
		// + tempToken + "\" at [" + (columnIndex + 2) + ", "
		// + (lineInFile - parsingStartLine)
		// + "]. Assigning NaN";
		// // MessageDialog.openError(new Shell(),
		// // "Error during parsing",
		// // sErrorMessage);
		//
		// Logger.log(new Status(IStatus.ERROR,
		// GeneralManager.PLUGIN_ID, sErrorMessage));
		// value = Float.NaN;
		// }
		//
		// floatArrays.get(floatIndex)[lineInFile - parsingStartLine] = value;
		//
		// floatIndex++;
		// columnIndex++;
		// break;
		// case STRING:
		// String token = strTokenLine.nextToken();
		// stringLists.get(stringIndex).add(token);
		// stringIndex++;
		// columnIndex++;
		// break;
		// case SKIP: // do nothing
		// strTokenLine.nextToken();
		// break;
		// case ABORT:
		// columnIndex = columnDataTypes.size();
		// break;
		// default:
		// throw new IllegalStateException(
		// "Unknown token pattern detected: "
		// + columnDataType.toString());
		// }
		//
		// // Check if the line is finished or early aborted
		// if (columnIndex == columnDataTypes.size()) {
		// continue;
		// }
		// }
		// }
		//
		// lineInFile++;
	}
	// @SuppressWarnings("unchecked")
	// protected void setArraysToDimensions() {
	//
	// int intArrayIndex = 0;
	// int floatArrayIndex = 0;
	// int stringArrayIndex = 0;
	// int dimensionIndex = 0;
	//
	// for (EColumnType dimensionType : columnDataTypes) {
	// // if(iDimensionIndex + 1 == targetDimensions.size())
	// // break;
	// switch (dimensionType) {
	// case INT:
	// targetDimensions.get(dimensionIndex).setRawData(
	// intArrays.get(intArrayIndex));
	// intArrayIndex++;
	// dimensionIndex++;
	// break;
	// case FLOAT:
	// targetDimensions.get(dimensionIndex).setRawData(
	// floatArrays.get(floatArrayIndex));
	// floatArrayIndex++;
	// dimensionIndex++;
	// break;
	// case CERTAINTY:
	// targetDimensions.get(dimensionIndex - 1).setUncertaintyData(
	// floatArrays.get(floatArrayIndex));
	// dataDomain.getTable().setContainsUncertaintyData(true);
	// floatArrayIndex++;
	// break;
	// case STRING:
	// ArrayList<String> rawStringData = stringLists.get(stringArrayIndex);
	// // rawStringData = fillUp(rawStringData);
	// ((NominalColumn<String>) targetDimensions.get(dimensionIndex))
	// .setRawNominalData(rawStringData);
	// // stringLists.add(new ArrayList<String>(iStopParsingAtLine -
	// // parsingStartLine));
	// stringArrayIndex++;
	// dimensionIndex++;
	// break;
	// case SKIP: // do nothing
	// break;
	// case ABORT:
	// return;
	//
	// default:
	// throw new IllegalStateException("Unknown token pattern detected: "
	// + dimensionType.toString());
	// }
	// }
	// }

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
