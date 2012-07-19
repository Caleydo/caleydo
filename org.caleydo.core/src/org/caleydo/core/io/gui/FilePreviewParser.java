/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;

/**
 * Parses the preview of a file and stores it in a data matrix.
 * 
 * @author Christian Partl
 * 
 */
public class FilePreviewParser {

	/**
	 * Matrix that stores the data for rows and all columns of the data file.
	 */
	private ArrayList<ArrayList<String>> dataMatrix = new ArrayList<ArrayList<String>>();

	/**
	 * The total number of columns detected in the file.
	 */
	private int totalNumberOfColumns;

	/**
	 * The total number of rows detected in the file.
	 */
	private int totalNumberOfRows;

	/**
	 * 
	 */
	public FilePreviewParser() {
	}

	/**
	 * Parses the specified file and stores the data in {@link #dataMatrix}.
	 * Additionally, the {@link #totalNumberOfRows} and
	 * {@link #totalNumberOfColumns} are set accordingly.
	 * 
	 * @param fileName
	 *            Filename that specifies the file to be parsed.
	 * @param delimiter
	 *            The delimiter that is used to separate columns
	 * @param parseAllRows
	 *            Determines whether all rows of the file should be parsed.
	 * @param maxRowsToParse
	 *            The number of rows that should be parsed if parseAllRows is
	 *            false.
	 */
	public void parse(String fileName, String delimiter, boolean parseAllRows,
			int maxRowsToParse) {

		for (List<String> row : dataMatrix) {
			row.clear();
		}
		dataMatrix.clear();

		// Read preview
		try {
			BufferedReader file = GeneralManager.get().getResourceLoader()
					.getResource(fileName);

			String line = "";
			totalNumberOfColumns = 0;
			totalNumberOfRows = 0;

			while ((line = file.readLine()) != null) {

				if (parseAllRows || (maxRowsToParse > totalNumberOfRows)) {

					String[] row = line.split(delimiter);
					int currentNumberOfColumns = row.length;
					ArrayList<String> currentDataRow = new ArrayList<String>(
							currentNumberOfColumns);

					for (int i = 0; i < currentNumberOfColumns; i++) {
						currentDataRow.add(row[i]);
					}

					if (currentNumberOfColumns > totalNumberOfColumns) {
						for (ArrayList<String> previousDataRow : dataMatrix) {
							int previousRowLength = previousDataRow.size();
							for (int i = 0; i < currentNumberOfColumns
									- previousRowLength; i++) {
								previousDataRow.add("");
							}
						}
						totalNumberOfColumns = currentNumberOfColumns;
					}

					if (currentNumberOfColumns < totalNumberOfColumns) {
						for (int i = 0; i < totalNumberOfColumns - currentNumberOfColumns; i++) {
							currentDataRow.add("");
						}
					}
					dataMatrix.add(currentDataRow);
				}
				totalNumberOfRows++;

			}

		} catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found!");
		} catch (IOException ioe) {
			throw new IllegalStateException("Input/output problem!");
		}

		// determineRowIDType();
		// updateTableColors();
		// updateTableInfoLabel();
		//
		// parentComposite.pack();
		//
		// updateWidgetsAccordingToTableChanges();
	}

	// private void readDataRow(String line, String delimiter, int rowIndex) {
	// // last flag triggers return of delimiter itself
	// StringTokenizer tokenizer = new StringTokenizer(line, delimiter, true);
	// TableItem item = new TableItem(previewTable, SWT.NONE);
	// item.setText("" + (rowIndex + 1)); // +1 to be intuitive for
	// // a non programmer :)
	// int colIndex = 0;
	// boolean isCellFilled = false;
	//
	// String[] dataRow = new String[totalNumberOfColumns];
	// dataMatrix[rowIndex] = dataRow;
	//
	// while (tokenizer.hasMoreTokens()) {
	// String nextToken = tokenizer.nextToken();
	//
	// // Check for empty cells
	// if (nextToken.equals(delimiter) && !isCellFilled) {
	// dataRow[colIndex] = "";
	// if (colIndex + 1 < previewTable.getColumnCount()) {
	// item.setText(colIndex + 1, dataRow[colIndex]);
	// }
	// colIndex++;
	// } else if (nextToken.equals(delimiter) && isCellFilled) {
	// isCellFilled = false; // reset
	// } else {
	// isCellFilled = true;
	// dataRow[colIndex] = nextToken;
	// if (colIndex + 1 < previewTable.getColumnCount()) {
	// item.setText(colIndex + 1, dataRow[colIndex]);
	// }
	// colIndex++;
	// }
	// }
	// }

	/**
	 * @return the dataMatrix, see {@link #dataMatrix}
	 */
	public ArrayList<ArrayList<String>> getDataMatrix() {
		return dataMatrix;
	}

	/**
	 * @return the totalNumberOfColumns, see {@link #totalNumberOfColumns}
	 */
	public int getTotalNumberOfColumns() {
		return totalNumberOfColumns;
	}

	/**
	 * @return the totalNumberOfRows, see {@link #totalNumberOfRows}
	 */
	public int getTotalNumberOfRows() {
		return totalNumberOfRows;
	}
}
