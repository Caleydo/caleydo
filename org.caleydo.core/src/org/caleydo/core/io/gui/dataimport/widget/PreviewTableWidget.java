/**
 *
 */
package org.caleydo.core.io.gui.dataimport.widget;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Manager for SWT tables to create and maintain preview tables for tabular data
 * that draws one row of buttons for selecting/deselecting columns on top and
 * one row enumeration column at the left.
 *
 * @author Christian Partl
 *
 */
public class PreviewTableWidget {
	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	public static final int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * Maximum number of previewed columns in {@link #previewTable}.
	 */
	public static final int MAX_PREVIEW_TABLE_COLUMNS = 10;

	// not static to release it early
	private final Color colorNormalRow = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private final Color colorId = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
	private final Color colorHeaderRow = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	private final Color colorBlack = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private final Color colorDeSelectedColumn = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	/**
	 * List of buttons, each created for one column to specify whether this column should be loaded or not.
	 */
	private final List<Button> selectedColumnButtons = new ArrayList<Button>();

	/**
	 * Table editors that are associated with {@link #selectedColumnButtons}.
	 */
	private final List<TableEditor> tableEditors = new ArrayList<TableEditor>();

	/**
	 * Number of header lines that was set the last time
	 * {@link #updateTableColors(int, int, int)} was called.
	 */
	private int oldNumberOfHeaderRows = -1;
	/**
	 * Index of the row that contains IDs that was set the last time
	 * {@link #updateTableColors(int, int, int)} was called.
	 */
	private int oldIDRowIndex = -1;
	/**
	 * Index of the row that contains IDs that was set the last time
	 * {@link #updateTableColors(int, int, int)} was called.
	 */
	private int oldIDColumnIndex = -1;

	/**
	 * Table that displays a preview of the data of the file specified by {@link #inputFileName}.
	 */
	private Table previewTable;

	/**
	 * Button to specify whether all columns of the data file should be shown in the {@link #previewTable}.
	 */
	private final Button showAllColumnsButton;

	/**
	 * Shows the total number columns in the data file and the number of displayed columns of the {@link #previewTable}.
	 */
	private final Label tableInfoLabel;


	public PreviewTableWidget(Composite parent, final BooleanCallback onSelectAllColumnsCallback) {
		previewTable = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		Composite tableInfoComposite = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		tableInfoComposite.setLayout(rowLayout);
		gridData = new GridData(SWT.RIGHT, SWT.TOP, true, false, 2, 1);
		gridData.heightHint = 20;
		tableInfoComposite.setLayoutData(gridData);

		tableInfoLabel = new Label(tableInfoComposite, SWT.NONE);

		new Label(tableInfoComposite, SWT.SEPARATOR | SWT.VERTICAL);

		showAllColumnsButton = new Button(tableInfoComposite, SWT.CHECK);
		showAllColumnsButton.setText("Show all Columns");
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(false);
		showAllColumnsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSelectAllColumnsCallback.on(showAllColumnsButton.getSelection());
			}
		});

		tableInfoComposite.pack(true);
	}

	/**
	 * Creates the {@link #previewTable} according to the {@link #dataMatrix}.
	 */
	public void createDataPreviewTableFromDataMatrix(List<? extends List<String>> dataMatrix, int numDataTableColumns) {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return;

		oldIDColumnIndex = -1;
		oldIDRowIndex = -1;
		oldNumberOfHeaderRows = -1;

		previewTable.removeAll();
		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		int numTableColumns = Math.min(dataMatrix.get(0).size(), numDataTableColumns) + 1;

		for (int i = 0; i < numTableColumns; i++) {
			TableColumn column = new TableColumn(previewTable, SWT.NONE);
			column.setWidth(100);
		}

		createUseColumnRow();

		for (int i = 0; i < dataMatrix.size(); i++) {
			List<String> dataRow = dataMatrix.get(i);
			TableItem item = new TableItem(previewTable, SWT.NONE);
			item.setText(0, "" + (i + 1));
			for (int j = 0; j < numTableColumns - 1; j++) {
				item.setText(j + 1, dataRow.get(j));
			}
		}

		// determineRowIDType();
		// updateTableColors();
		// updateTableInfoLabel();

	}

	/**
	 * Colors the header rows gray and the id row and id column green.
	 *
	 * @param numberOfHeaderRows
	 *            Number of rows that should be treated as headers.
	 * @param idRowIndex
	 *            Index of the row that contains IDs. If no row shall be
	 *            colored, set -1.
	 * @param idColumnIndex
	 *            Index of the column that contains IDs. If no column shall be
	 *            colored, set -1.
	 */
	public void updateTableColors(int numberOfHeaderRows, int idRowIndex, int idColumnIndex) {

		if (oldNumberOfHeaderRows != -1 && oldNumberOfHeaderRows < previewTable.getItemCount()) {
			for (int i = 1; i < oldNumberOfHeaderRows + 1; i++) {
				setTableRowBackground(i, colorNormalRow);
			}
		}

		if (oldIDRowIndex != -1 && oldIDRowIndex < previewTable.getItemCount()) {
			if (oldNumberOfHeaderRows <= oldIDRowIndex) {
				setTableRowBackground(oldIDRowIndex, colorNormalRow);
			}
		}

		if (oldIDColumnIndex != -1 && oldIDColumnIndex < previewTable.getColumnCount()) {
			setTableColumnBackground(oldIDColumnIndex + 1, colorNormalRow);
			Button selectionButton = selectedColumnButtons.get(oldIDColumnIndex);
			selectionButton.setVisible(true);
			if (!selectionButton.getSelection()) {
				setTableColumnForeground(oldIDColumnIndex + 1, colorDeSelectedColumn);
			}
		}

		if (numberOfHeaderRows < previewTable.getItemCount()) {
			for (int i = 1; i < numberOfHeaderRows + 1; i++) {
				setTableRowBackground(i, colorHeaderRow);
			}
		}

		if (idRowIndex != -1 && idRowIndex < previewTable.getItemCount()) {
			setTableRowBackground(idRowIndex + 1, colorId);
		}

		if (idColumnIndex != -1 && idColumnIndex < getColumnCount()) {
			setTableColumnBackground(idColumnIndex + 1, colorId);
			selectedColumnButtons.get(idColumnIndex).setVisible(false);
			setTableColumnForeground(idColumnIndex + 1, colorBlack);
		}

		oldIDRowIndex = idRowIndex;
		oldIDColumnIndex = idColumnIndex;
		oldNumberOfHeaderRows = numberOfHeaderRows;
	}

	private void setTableRowBackground(int rowIndex, Color color) {
		TableItem item = previewTable.getItem(rowIndex);
		for (int i = 0; i < previewTable.getColumnCount(); i++) {
			item.setBackground(i, color);
		}
	}

	private void setTableColumnBackground(int columnIndex, Color color) {
		for (int i = 1; i < previewTable.getItemCount(); i++) {
			previewTable.getItem(i).setBackground(columnIndex, color);
		}
	}

	private void setTableColumnForeground(int columnIndex, Color color) {
		for (int i = 1; i < previewTable.getItemCount(); i++) {
			previewTable.getItem(i).setForeground(columnIndex, color);
		}
	}

	private void createUseColumnRow() {

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE);
		tmpItem.setText("Use column");

		for (Button button : selectedColumnButtons) {
			button.dispose();
		}
		selectedColumnButtons.clear();
		for (TableEditor editor : tableEditors) {
			editor.dispose();
		}
		tableEditors.clear();

		Button skipButton;
		for (int colIndex = 1; colIndex < previewTable.getColumnCount(); colIndex++) {
			skipButton = new Button(previewTable, SWT.CHECK | SWT.CENTER);
			skipButton.setSelection(true);
			skipButton.setData("column", colIndex);
			skipButton.setText("" + colIndex);
			skipButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean skipColumn = !((Button) e.widget).getSelection();
					setTableColumnForeground((Integer) e.widget.getData("column"), skipColumn ? colorDeSelectedColumn
							: colorBlack);
				}
			});

			selectedColumnButtons.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, colIndex);
			tableEditors.add(editor);
		}
	}

	/**
	 * @return the selectedColumnButtons, see {@link #selectedColumnButtons}
	 */
	public List<Button> getSelectedColumnButtons() {
		return selectedColumnButtons;
	}

	public void selectColumns(boolean selectAll, int columnOfRowId) {

		if (selectAll) {
			for (int i = 0; i < selectedColumnButtons.size(); i++) {
				Button button = selectedColumnButtons.get(i);
				button.setSelection(true);
				setTableColumnForeground(i + 1, colorBlack);
			}
		} else {
			for (int i = 0; i < selectedColumnButtons.size(); i++) {
				Button button = selectedColumnButtons.get(i);
				button.setSelection(false);
				if (i != columnOfRowId) {
					setTableColumnForeground(i + 1, colorDeSelectedColumn);
				}
			}
		}
	}

	/**
	 * @param selectedColumns
	 */
	public void setSelectedColumns(Collection<Integer> selectedColumns) {
		BitSet s = new BitSet();
		for(Integer c : selectedColumns)
			s.set(c);
		for (int i = 0; i < selectedColumnButtons.size(); ++i) {
			selectedColumnButtons.get(i).setSelection(s.get(i));
		}
	}


	public void updateVisibleColumns(int totalNumberOfColumns) {
		int visibleColumns = getColumnCount();
		showAllColumnsButton.setEnabled(visibleColumns <= totalNumberOfColumns);
		tableInfoLabel.setText(visibleColumns + " of " + totalNumberOfColumns + " Columns shown");
		tableInfoLabel.getParent().getParent().layout(true, true);
	}

	public int getColumnCount() {
		return selectedColumnButtons.size();
	}

	public int getRowCount() {
		return previewTable.getItemCount() - 1; // -1 for th
	}

	public Collection<Integer> getSelectedColumns() {
		Collection<Integer> result = new ArrayList<>();
		for (int i = 0; i < selectedColumnButtons.size(); ++i) {
			if (selectedColumnButtons.get(i).getSelection())
				result.add(i);
		}
		return result;
	}

}
