/**
 *
 */
package org.caleydo.core.io.gui.dataimport;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Mediator for {@link ImportGroupingDialog}. This class is responsible for
 * setting the states of all widgets of the dialog and triggering actions
 * according to different events that occur in the dialog.
 *
 * @author Christian Partl
 *
 */
public class ImportGroupingDialogMediator {

	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	protected static final int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * Maximum number of previewed columns in {@link #previewTable}.
	 */
	protected static final int MAX_PREVIEW_TABLE_COLUMNS = 10;

	/**
	 * The maximum number of ids that are tested in order to determine the
	 * {@link IDType}.
	 */
	protected static final int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	/**
	 * The row id category for which groupings should be loaded.
	 */
	protected IDCategory rowIDCategory;

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and
	 * all columns of the data file.
	 */
	protected ArrayList<ArrayList<String>> dataMatrix;

	/**
	 * The total number of columns of the input file.
	 */
	protected int totalNumberOfColumns;

	/**
	 * The total number of rows of the input file.
	 */
	protected int totalNumberOfRows;

	/**
	 * The dialog this class serves as mediator for.
	 */
	private ImportGroupingDialog dialog;

	/**
	 * The {@link GroupingParseSpecification} created using this {@link #dialog}
	 * .
	 */
	private GroupingParseSpecification groupingParseSpecification;

	/**
	 * Determines whether all columns of the data file shall be shown in the
	 * {@link #previewTable}.
	 */
	protected boolean showAllColumns = false;

	/**
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	/**
	 * Manager for {@link #previewTable} that extends its features.
	 */
	private PreviewTableManager previewTableManager;

	/**
	 * Determines, whether the widgets should be initialized from the
	 * {@link #groupingParseSpecification}.
	 */
	private boolean initFromGroupParseSpecification;

	/**
	 */
	public ImportGroupingDialogMediator(ImportGroupingDialog dialog,
			IDCategory rowIDCategory) {
		this.dialog = dialog;
		this.rowIDCategory = rowIDCategory;
		initFromGroupParseSpecification = false;
		groupingParseSpecification = new GroupingParseSpecification();
		groupingParseSpecification.setDelimiter("\t");
		groupingParseSpecification.setNumberOfHeaderLines(1);
	}

	/**
	 * @param groupingParseSpecification
	 *            {@link GroupingParseSpecification} that will be used to
	 *            initialize the widgets of this dialog.
	 */
	public ImportGroupingDialogMediator(ImportGroupingDialog dialog,
			GroupingParseSpecification groupingParseSpecification,
			IDCategory rowIDCategory) {
		this.dialog = dialog;
		this.rowIDCategory = rowIDCategory;
		this.groupingParseSpecification = new GroupingParseSpecification();
		this.groupingParseSpecification
				.setColumnIDSpecification(groupingParseSpecification
						.getColumnIDSpecification());
		this.groupingParseSpecification.setColumnOfRowIds(groupingParseSpecification
				.getColumnOfRowIds());
		this.groupingParseSpecification.setColumns(groupingParseSpecification
				.getColumns());
		this.groupingParseSpecification.setContainsColumnIDs(groupingParseSpecification
				.isContainsColumnIDs());
		this.groupingParseSpecification.setDataSourcePath(groupingParseSpecification
				.getDataSourcePath());
		this.groupingParseSpecification.setDelimiter(groupingParseSpecification
				.getDelimiter());
		this.groupingParseSpecification.setGroupingName(groupingParseSpecification
				.getGroupingName());
		this.groupingParseSpecification.setNumberOfHeaderLines(groupingParseSpecification
				.getNumberOfHeaderLines());
		this.groupingParseSpecification.setRowIDSpecification(groupingParseSpecification
				.getRowIDSpecification());
		this.groupingParseSpecification.setRowOfColumnIDs(groupingParseSpecification
				.getRowOfColumnIDs());
		if (groupingParseSpecification.getDataSourcePath() != null) {
			File file = new File(groupingParseSpecification.getDataSourcePath());
			initFromGroupParseSpecification = file.exists();
		} else {
			initFromGroupParseSpecification = false;
		}

	}

	/**
	 * Checks if all required fields of the {@link #dialog} are filled. If so
	 * the {@link #groupingParseSpecification} is filled.
	 *
	 * @return True, when all required fields were filled, false otherwise.
	 */
	public boolean okPressed() {
		if (dialog.loadFile.getFileName().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename",
					"Please specify a file to load");
			return false;
		}

		if (dialog.rowConfig.getIDType() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row ID Type",
					"Please select the ID type of the rows");
			return false;
		}

		ArrayList<Integer> selectedColumns = new ArrayList<Integer>();
		for (int columnIndex = 0; columnIndex < totalNumberOfColumns; columnIndex++) {

			if (groupingParseSpecification.getColumnOfRowIds() != columnIndex) {
				if (columnIndex + 1 < dialog.previewTable.getColumnCount()) {
					if (dialog.selectedColumnButtons.get(columnIndex).getSelection()) {
						selectedColumns.add(columnIndex);
					}
				} else {
					selectedColumns.add(columnIndex);
				}
			}
		}
		groupingParseSpecification.setColumns(selectedColumns);
		IDSpecification rowIDSpecification = new IDSpecification();
		IDType rowIDType = dialog.rowConfig.getIDType();
		rowIDSpecification.setIdType(rowIDType.toString());
		if (rowIDType.getIDCategory().getCategoryName().equals("GENE"))
			rowIDSpecification.setIDTypeGene(true);
		rowIDSpecification.setIdCategory(rowIDType.getIDCategory().toString());
		if (rowIDType.getTypeName().equalsIgnoreCase("REFSEQ_MRNA")) {
			// for REFSEQ_MRNA we ignore the .1, etc.
			IDTypeParsingRules parsingRules = new IDTypeParsingRules();
			parsingRules.setSubStringExpression("\\.");
			parsingRules.setDefault(true);
			rowIDSpecification.setIdTypeParsingRules(parsingRules);
		}
		groupingParseSpecification.setRowIDSpecification(rowIDSpecification);
		groupingParseSpecification.setContainsColumnIDs(false);
		groupingParseSpecification.setGroupingName(dialog.label.getText());

		return true;
	}

	public void onSelectFile(String inputFileName) {
		dialog.label.setText(inputFileName.substring(
				inputFileName.lastIndexOf(File.separator) + 1,
				inputFileName.lastIndexOf(".")));

		groupingParseSpecification.setDataSourcePath(inputFileName);

		dialog.label.setEnabled(true);
		dialog.rowConfig.setEnabled(true);

		dialog.delimiterRadioGroup.setEnabled(true);

		dialog.selectAllNone.setEnabled(true);
		dialog.showAllColumnsButton.setEnabled(true);

		createDataPreviewTableFromFile(true);
	}

	/**
	 * Selects all columns of the preview table of the {@link #dialog}.
	 */
	public void selectAllButtonPressed() {
		for (int i = 0; i < dialog.selectedColumnButtons.size(); i++) {
			Button button = dialog.selectedColumnButtons.get(i);
			button.setSelection(true);
			previewTableManager.colorTableColumnText(i + 1, Display.getCurrent()
					.getSystemColor(SWT.COLOR_BLACK));
		}
	}

	/**
	 * Unselects all columns of the preview table of the {@link #dialog}.
	 */
	public void selectNoneButtonPressed() {
		for (int i = 0; i < dialog.selectedColumnButtons.size(); i++) {
			Button button = dialog.selectedColumnButtons.get(i);
			button.setSelection(false);
			if (i != groupingParseSpecification.getColumnOfRowIds())
				previewTableManager.colorTableColumnText(i + 1, Display.getCurrent()
						.getSystemColor(SWT.COLOR_GRAY));
		}
	}

	/**
	 * Initializes all widgets of the {@link #dialog}. This method should be
	 * called after all widgets of the dialog were created.
	 */
	public void guiCreated() {
		previewTableManager = new PreviewTableManager(dialog.previewTable);

		if (initFromGroupParseSpecification) {
			initWidgetsFromGroupParseSpecification();
		} else {
			initWidgetsWithDefaultValues();
		}
	}

	private void initWidgetsFromGroupParseSpecification() {
		dialog.loadFile.setFileName(groupingParseSpecification.getDataSourcePath());

		dialog.label
				.setText(groupingParseSpecification.getGroupingName());
		dialog.label.setEnabled(true);

		dialog.rowConfig.setCategoryID(rowIDCategory);
		dialog.rowConfig.setNumHeaderRows(groupingParseSpecification.getNumberOfHeaderLines());
		dialog.rowConfig.setColumnOfRowIds(groupingParseSpecification.getColumnOfRowIds() + 1);

		dialog.delimiterRadioGroup.setDelimeter(groupingParseSpecification.getDelimiter());

		dialog.selectAllNone.setEnabled(true);

		ArrayList<Integer> selectedColumns = groupingParseSpecification.getColumns();

		int maxColumnIndex = 0;
		for (Integer columnIndex : selectedColumns) {
			if (columnIndex > maxColumnIndex)
				maxColumnIndex = columnIndex;
		}
		if (maxColumnIndex + 1 > MAX_PREVIEW_TABLE_COLUMNS) {
			createDataPreviewTableFromFile(false);
		} else {
			createDataPreviewTableFromFile(true);
		}
		selectNoneButtonPressed();

		for (Integer columnIndex : selectedColumns) {
			dialog.selectedColumnButtons.get(columnIndex).setSelection(true);
		}

		dialog.rowConfig.setIDTypes(getPublicIDTypes(rowIDCategory),
				IDType.getIDType(groupingParseSpecification.getRowIDSpecification().getIdType()));
	}

	private void initWidgetsWithDefaultValues() {

		dialog.loadFile.setFileName("");

		dialog.label.setText("");
		dialog.label.setEnabled(false);

		dialog.rowConfig.setCategoryID(rowIDCategory);
		dialog.rowConfig.setIDTypes(getPublicIDTypes(rowIDCategory), null);
		dialog.rowConfig.setEnabled(false);

		dialog.delimiterRadioGroup.setEnabled(false);

		dialog.selectAllNone.setEnabled(false);

		dialog.showAllColumnsButton.setEnabled(false);
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onNumHeaderRowsChanged(int numHeaderRows) {
		try {
			groupingParseSpecification.setNumberOfHeaderLines(numHeaderRows);
			previewTableManager.updateTableColors(
					groupingParseSpecification.getNumberOfHeaderLines(), -1,
					groupingParseSpecification.getColumnOfRowIds() + 1);
		} catch (NumberFormatException exc) {

		}

	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onColumnOfRowIDChanged(int column) {
		groupingParseSpecification.setColumnOfRowIds(column - 1);
		previewTableManager.updateTableColors(groupingParseSpecification.getNumberOfHeaderLines(), -1,
				groupingParseSpecification.getColumnOfRowIds() + 1);
	}

	/**
	 * Loads all columns or the {@link #MAX_PREVIEW_TABLE_COLUMNS} into the
	 * preview table, depending on the state of showAllColumnsButton of the
	 * {@link #dialog}.
	 */
	public void showAllColumnsButtonPressed() {
		showAllColumns = dialog.showAllColumnsButton.getSelection();
		previewTableManager.createDataPreviewTableFromDataMatrix(dataMatrix,
				showAllColumns ? totalNumberOfColumns : MAX_PREVIEW_TABLE_COLUMNS);
		dialog.selectedColumnButtons = previewTableManager.getSelectedColumnButtons();
		// determineRowIDType();
		previewTableManager.updateTableColors(
				groupingParseSpecification.getNumberOfHeaderLines(), -1,
				groupingParseSpecification.getColumnOfRowIds() + 1);
		updateWidgetsAccordingToTableChanges();
	}

	public void onDelimiterChanged(String delimiter) {
		groupingParseSpecification.setDelimiter(delimiter);
		createDataPreviewTableFromFile(true);
	}


	/**
	 * Creates the preview table from the file specified by
	 * {@link #groupingParseSpecification}. Widgets of the {@link #dialog} are
	 * updated accordingly.
	 *
	 * @param showOnlyPreviewColumns
	 *            Determines whether {@link #MAX_PREVIEW_TABLE_COLUMNS}, or all
	 *            columns of the file are shown.
	 */
	public void createDataPreviewTableFromFile(boolean showOnlyPreviewColumns) {
		parser.parse(groupingParseSpecification.getDataSourcePath(),
				groupingParseSpecification.getDelimiter(), false, MAX_PREVIEW_TABLE_ROWS);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		totalNumberOfRows = parser.getTotalNumberOfRows();
		previewTableManager
				.createDataPreviewTableFromDataMatrix(dataMatrix,
						showOnlyPreviewColumns ? MAX_PREVIEW_TABLE_COLUMNS
								: totalNumberOfColumns);
		dialog.selectedColumnButtons = previewTableManager.getSelectedColumnButtons();
		updateWidgetsAccordingToTableChanges();
		determineRowIDType();
		guessNumberOfHeaderRows();
		previewTableManager.updateTableColors(
				groupingParseSpecification.getNumberOfHeaderLines(), -1,
				groupingParseSpecification.getColumnOfRowIds() + 1);

		dialog.parentComposite.pack();
	}

	private void guessNumberOfHeaderRows() {
		// In grouping case we can have 0 header rows as there does not have to
		// be an id row
		int numHeaderRows = 0;
		for (int i = 0; i < dataMatrix.size(); i++) {
			ArrayList<String> row = dataMatrix.get(i);
			int numFloatsFound = 0;
			for (int j = 0; j < row.size() && j < MAX_PREVIEW_TABLE_COLUMNS; j++) {
				String text = row.get(j);
				try {
					// This currently only works for numerical values
					Float.parseFloat(text);
					numFloatsFound++;
					if (numFloatsFound >= 3) {
						dialog.rowConfig.setNumHeaderRows(numHeaderRows);
						return;
					}
				} catch (Exception e) {

				}
			}
			numHeaderRows++;
		}
	}

	private void determineRowIDType() {
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < dataMatrix.size()
				&& i < MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION; i++) {
			ArrayList<String> row = dataMatrix.get(i);
			idList.add(row.get(groupingParseSpecification.getColumnOfRowIds()));
		}

		float maxProbability = 0;
		IDType mostProbableIDType = null;
		for (IDCategory idCategory : getAvailableIDCategories()) {
			List<Pair<Float, IDType>> probabilityList = idCategory
					.getListOfIDTypeAffiliationProbabilities(idList, false);
			if (probabilityList.size() > 0) {
				Pair<Float, IDType> pair = probabilityList.get(0);
				if (pair.getFirst() > maxProbability) {
					maxProbability = pair.getFirst();
					mostProbableIDType = pair.getSecond();
				}
			}
		}

		if (maxProbability < 0.0001f)
			mostProbableIDType = null;

		setMostProbableRecordIDType(mostProbableIDType);
	}

	protected void setMostProbableRecordIDType(IDType mostProbableRecordIDType) {
		dialog.rowConfig.setIDTypes(getPublicIDTypes(rowIDCategory), mostProbableRecordIDType);
	}

	protected void updateWidgetsAccordingToTableChanges() {
		dialog.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		// showAllColumnsButton.setSelection(false);
		if (totalNumberOfColumns == (dialog.previewTable.getColumnCount() - 1)) {
			dialog.showAllColumnsButton.setSelection(true);
		} else {
			dialog.showAllColumnsButton.setSelection(false);
		}
		if (totalNumberOfColumns <= MAX_PREVIEW_TABLE_COLUMNS) {
			dialog.showAllColumnsButton.setEnabled(false);
		} else {
			dialog.showAllColumnsButton.setEnabled(true);
		}
		dialog.tableInfoLabel.setText((dialog.previewTable.getColumnCount() - 1) + " of "
				+ totalNumberOfColumns + " Columns shown");
		dialog.tableInfoLabel.pack();
		dialog.tableInfoLabel.getParent().pack(true);
		dialog.parentComposite.pack(true);
		dialog.parentComposite.layout(true);
	}

	private static List<IDType> getPublicIDTypes(IDCategory idCategory) {
		List<IDType> idTypes = new ArrayList<>(idCategory.getIdTypes());

		for (Iterator<IDType> it = idTypes.iterator(); it.hasNext();)
			if (it.next().isInternalType())
				it.remove();
		return idTypes;
	}

	protected ArrayList<IDCategory> getAvailableIDCategories() {
		ArrayList<IDCategory> idCategories = new ArrayList<IDCategory>();
		idCategories.add(rowIDCategory);
		return idCategories;
	}

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return groupingParseSpecification;
	}


}
