/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.io.FileUtil;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.SelectAllNoneWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.INoArgumentCallback;
import org.caleydo.core.io.gui.dataimport.widget.table.PreviewTableWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.PreviewTableWidget.RowColDesc;
import org.caleydo.core.util.base.BooleanCallback;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PreviewTable {
	private final MatrixDefinition spec;

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and all columns of the data file.
	 */
	private List<List<String>> dataMatrix;

	/**
	 * The total number of columns of the input file.
	 */
	private int totalNumberOfColumns;

	/**
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	private DelimiterWidget delimeter;

	private SelectAllNoneWidget selectAllNone;

	private PreviewTableWidget previewTable;

	private final IPreviewCallback previewCallback;

	private boolean isTransposed = false;

	private IDTypeParsingRules columnIDTypeParsingRules;
	private IDTypeParsingRules rowIDTypeParsingRules;

	private File transposedDataFile;

	private String originalFilePath;

	public PreviewTable(Composite parent, MatrixDefinition spec, IPreviewCallback previewCallback,
			boolean isTransposeable, boolean areColumnsSelectable) {
		this.spec = spec;
		this.previewCallback = previewCallback;

		delimeter = new DelimiterWidget(parent, new ICallback<String>() {
			@Override
			public void on(String delimiter) {
				onDelimiterChanged(delimiter);
			}
		});
		delimeter.setDelimeter(spec.getDelimiter());

		selectAllNone = new SelectAllNoneWidget(parent, new BooleanCallback() {
			@Override
			public void on(boolean selectAll) {
				onSelectAllNone(selectAll);
			}
		});
		this.selectAllNone.setEnabled(false);

		previewTable = new PreviewTableWidget(parent, areColumnsSelectable, null, isTransposeable,
				new INoArgumentCallback() {

					@Override
					public void on() {
						isTransposed = !isTransposed;

						if (isTransposed) {
							if (transposedDataFile == null) {
								loadTransposedFile();
							}
							originalFilePath = PreviewTable.this.spec.getDataSourcePath();
							PreviewTable.this.spec.setDataSourcePath(transposedDataFile.getAbsolutePath());
						} else {
							PreviewTable.this.spec.setDataSourcePath(originalFilePath);
						}
						createDataPreviewTableFromFile();
					}

				});
		// , new BooleanCallback() {
		// @Override
		// public void on(boolean data) {
		// onShowAllColumns(data);
		// }
		// });
	}

	/**
	 * @return the dataMatrix, see {@link #dataMatrix}
	 */
	public List<List<String>> getDataMatrix() {
		return dataMatrix;
	}

	public Composite getTable() {
		return this.previewTable.getTable();
	}

	public void addCustomHeaderRow(IDataProvider dataProvider, boolean isEditable) {
		previewTable.addCustomHeaderRows(dataProvider, isEditable);
	}

	public void clearCustomHeaderRows() {
		previewTable.clearCustomHeaderRows();
	}

	private void loadTransposedFile() {
		try {
			transposedDataFile = File.createTempFile("tmptransposed", "txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileUtil.transposeCSV(spec.getDataSourcePath(), transposedDataFile.getAbsolutePath(), spec.getDelimiter());
	}

	/**
	 * Creates the preview table from the file specified by {@link #groupingParseSpecification}. Widgets of the
	 * {@link #dialog} are updated accordingly.
	 *
	 */
	public void createDataPreviewTableFromFile() {
		parser.parseWithProgress(Display.getCurrent().getActiveShell(),
				isTransposed ? transposedDataFile.getAbsolutePath() : spec.getDataSourcePath(), spec.getDelimiter(),
				true, PreviewTableWidget.MAX_PREVIEW_TABLE_ROWS);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		this.previewTable.createTableFromMatrix(dataMatrix, totalNumberOfColumns);
		previewCallback.on(totalNumberOfColumns, parser.getTotalNumberOfRows(), dataMatrix);
		// previewTable.updateVisibleColumns(totalNumberOfColumns);
		updateTable();
		// this.previewTable.updateTableColors(spec.getNumberOfHeaderLines(),
		// spec.getRowOfColumnIDs() == null ? -1 : spec.getRowOfColumnIDs(), spec.getColumnOfRowIds());
	}

	private void updateTable() {
		previewTable.updateTable(spec.getNumberOfHeaderLines(), Lists.newArrayList(new RowColDesc(spec
				.getRowOfColumnIDs() == null ? -1 : spec.getRowOfColumnIDs(), Color.GREEN, rowIDTypeParsingRules)),
				Lists.newArrayList(new RowColDesc(spec.getColumnOfRowIds(), Color.GREEN, columnIDTypeParsingRules)));
	}

	public Collection<Integer> getSelectedColumns() {
		ArrayList<Integer> columns = new ArrayList<>(previewTable.getSelectedColumns());

		if (columns.get(columns.size() - 1) == -1) {
			columns.remove(columns.size() - 1);
			int from = previewTable.getColumnCount(); // everything before was directly selected
			int to = this.totalNumberOfColumns; // all possible
			for (int i = from; i < to; ++i) {
				columns.add(i);
			}
		}
		return columns;
	}

	public String getValue(int rowIndex, int columnIndex) {
		if (previewTable == null)
			return null;
		return previewTable.getValue(rowIndex, columnIndex);
	}

	/**
	 * @param selectedColumns
	 *
	 */
	public void generatePreview(List<Integer> selectedColumns) {
		this.delimeter.setEnabled(true);
		this.selectAllNone.setEnabled(true);

		createDataPreviewTableFromFile();
		// int maxColumnIndex = 0;
		// for (Integer columnIndex : selectedColumns) {
		// if (columnIndex > maxColumnIndex)
		// maxColumnIndex = columnIndex;
		// }
		// if (maxColumnIndex + 1 > PreviewTableWidget.MAX_PREVIEW_TABLE_COLUMNS) {
		// createDataPreviewTableFromFile(false);
		// } else {
		// createDataPreviewTableFromFile(true);
		// }
		previewTable.setSelectedColumns(selectedColumns);
	}

	public void generatePreview(boolean fileChanged) {
		if (fileChanged) {
			isTransposed = false;
			transposedDataFile = null;
		}
		this.delimeter.setEnabled(true);
		this.selectAllNone.setEnabled(true);
		createDataPreviewTableFromFile();
	}

	public void onSelectAllNone(boolean selectAll) {
		this.previewTable.selectColumns(selectAll, spec.getColumnOfRowIds());
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onNumHeaderRowsChanged(int numHeaderRows) {
		try {
			spec.setNumberOfHeaderLines(numHeaderRows);
			updateTable();
			// previewTable.updateTableColors(spec.getNumberOfHeaderLines(),
			// spec.getRowOfColumnIDs() == null ? -1 : spec.getRowOfColumnIDs(), spec.getColumnOfRowIds());
		} catch (NumberFormatException exc) {

		}

	}

	public void setColumnIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		this.columnIDTypeParsingRules = idTypeParsingRules;
		updateTable();
		// previewTable.setColumnIDTypeParsingRules(idTypeParsingRules);
	}

	public void setRowIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		this.rowIDTypeParsingRules = idTypeParsingRules;
		// previewTable.setRowIDTypeParsingRules(idTypeParsingRules);
		updateTable();
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onColumnOfRowIDChanged(int column) {
		spec.setColumnOfRowIds(column - 1);
		updateTable();
		// previewTable.updateTableColors(spec.getNumberOfHeaderLines(),
		// spec.getRowOfColumnIDs() == null ? -1 : spec.getRowOfColumnIDs(), spec.getColumnOfRowIds());
	}

	public void onRowOfColumnIDChanged(int row) {
		spec.setRowOfColumnIDs(row > 0 ? row - 1 : null);
		updateTable();
		// previewTable.updateTableColors(spec.getNumberOfHeaderLines(),
		// spec.getRowOfColumnIDs() == null ? -1 : spec.getRowOfColumnIDs(), spec.getColumnOfRowIds());
	}

	public void onDelimiterChanged(String delimiter) {
		spec.setDelimiter(delimiter);
		if (isTransposed) {
			loadTransposedFile();
			spec.setDataSourcePath(transposedDataFile.getAbsolutePath());
		}
		createDataPreviewTableFromFile();
	}

	// /**
	// * Loads all columns or the {@link #MAX_PREVIEW_TABLE_COLUMNS} into the preview table, depending on the state of
	// * showAllColumnsButton of the {@link #dialog}.
	// */
	// public void onShowAllColumns(boolean showAllColumns) {
	// this.previewTable.createDataPreviewTableFromDataMatrix(dataMatrix, totalNumberOfColumns);
	// // determineRowIDType();
	// this.previewTable.updateTableColors(spec.getNumberOfHeaderLines(), -1, spec.getColumnOfRowIds());
	// // this.previewTable.updateVisibleColumns(totalNumberOfColumns);
	// }

	public interface IPreviewCallback {
		public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix);
	}

	public int getNumRows() {
		if (dataMatrix == null)
			return 0;
		return dataMatrix.size();
	}

	public int getNumColumns() {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return 0;
		return dataMatrix.get(0).size();
	}
}
