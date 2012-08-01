/**
 * 
 */
package org.caleydo.core.io.gui.dataimport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
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
public class PreviewTableManager {

	/**
	 * List of buttons, each created for one column to specify whether this
	 * column should be loaded or not.
	 */
	protected ArrayList<Button> selectedColumnButtons = new ArrayList<Button>();

	/**
	 * Table editors that are associated with {@link #selectedColumnButtons}.
	 */
	protected ArrayList<TableEditor> tableEditors = new ArrayList<TableEditor>();

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
	 * The preview table.
	 */
	private Table previewTable;

	/**
	 * @param parent
	 * @param style
	 */
	public PreviewTableManager(Table previewTable) {
		this.previewTable = previewTable;
	}

	/**
	 * Creates the {@link #previewTable} according to the {@link #dataMatrix}.
	 */
	public void createDataPreviewTableFromDataMatrix(
			ArrayList<ArrayList<String>> dataMatrix, int numDataTableColumns) {

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
	public void updateTableColors(int numberOfHeaderRows, int idRowIndex,
			int idColumnIndex) {

		// colorTableRow(0,
		// Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		// colorTableColumn(0,
		// Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));

		if (oldNumberOfHeaderRows != -1
				&& oldNumberOfHeaderRows < previewTable.getItemCount()) {
			for (int i = 1; i < oldNumberOfHeaderRows + 1; i++) {
				colorTableRow(i, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			}
		}

		if (oldIDRowIndex != -1 && oldIDRowIndex < previewTable.getItemCount()) {
			if (oldNumberOfHeaderRows <= oldIDRowIndex) {
				colorTableRow(oldIDRowIndex,
						Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			}
		}

		if (oldIDColumnIndex != -1 && oldIDColumnIndex < previewTable.getColumnCount()) {
			colorTableColumn(oldIDColumnIndex,
					Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			Button selectionButton = selectedColumnButtons.get(oldIDColumnIndex - 1);
			selectionButton.setVisible(true);
			if (!selectionButton.getSelection()) {
				colorTableColumnText(oldIDColumnIndex, Display.getCurrent()
						.getSystemColor(SWT.COLOR_GRAY));
			}
		}

		if (numberOfHeaderRows < previewTable.getItemCount()) {
			for (int i = 1; i < numberOfHeaderRows + 1; i++) {
				colorTableRow(i, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
		}

		if (idRowIndex != -1 && idRowIndex < previewTable.getItemCount()) {
			colorTableRow(idRowIndex, Display.getCurrent()
					.getSystemColor(SWT.COLOR_GREEN));
		}

		if (idColumnIndex != -1 && idColumnIndex < previewTable.getColumnCount()) {
			colorTableColumn(idColumnIndex,
					Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
			selectedColumnButtons.get(idColumnIndex - 1).setVisible(false);
			colorTableColumnText(idColumnIndex, Display.getCurrent()
					.getSystemColor(SWT.COLOR_BLACK));
		}

		oldIDRowIndex = idRowIndex;
		oldIDColumnIndex = idColumnIndex;
		oldNumberOfHeaderRows = numberOfHeaderRows;
	}

	public void colorTableRow(int rowIndex, Color color) {
		TableItem item = previewTable.getItem(rowIndex);
		for (int i = 0; i < previewTable.getColumnCount(); i++) {
			item.setBackground(i, color);
		}
	}

	public void colorTableColumn(int columnIndex, Color color) {
		for (int i = 1; i < previewTable.getItemCount(); i++) {
			previewTable.getItem(i).setBackground(columnIndex, color);
		}
	}

	public void colorTableColumnText(int columnIndex, Color color) {
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
					Color textColor = null;
					boolean bSkipColumn = !((Button) e.widget).getSelection();

					if (bSkipColumn) {
						textColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
					} else {
						textColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
					}

					colorTableColumnText((Integer) e.widget.getData("column"), textColor);
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
	public ArrayList<Button> getSelectedColumnButtons() {
		return selectedColumnButtons;
	}

}
