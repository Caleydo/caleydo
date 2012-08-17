/**
 * 
 */
package org.caleydo.core.io.gui.dataimport;

import java.io.File;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	protected ArrayList<IDType> rowIDTypes = new ArrayList<IDType>();

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
		if (dialog.fileNameTextField.getText().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename",
					"Please specify a file to load");
			return false;
		}

		if (dialog.rowIDCombo.getSelectionIndex() == -1) {
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
		IDType rowIDType = IDType.getIDType(dialog.rowIDCombo.getItem(dialog.rowIDCombo
				.getSelectionIndex()));
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
		groupingParseSpecification
				.setGroupingName(dialog.groupingNameTextField.getText());

		return true;
	}

	/**
	 * Opens a file dialog to specify the file that defines the groupings.
	 */
	public void openFileButtonPressed() {
		FileDialog fileDialog = new FileDialog(new Shell());
		fileDialog.setText("Open");
		// fileDialog.setFilterPath(filePath);
		String[] filterExt = { "*.csv;*.txt;*.gct", "*.*" };
		fileDialog.setFilterExtensions(filterExt);

		String inputFileName = fileDialog.open();

		if (inputFileName == null)
			return;
		dialog.fileNameTextField.setText(inputFileName);
		dialog.groupingNameTextField.setText(inputFileName.substring(
				inputFileName.lastIndexOf(File.separator) + 1,
				inputFileName.lastIndexOf(".")));

		groupingParseSpecification.setDataSourcePath(inputFileName);

		dialog.fileNameTextField.setEnabled(false);
		dialog.groupingNameTextField.setEnabled(true);
		dialog.rowIDCombo.setEnabled(true);
		dialog.numHeaderRowsSpinner.setEnabled(true);
		dialog.columnOfRowIDSpinner.setEnabled(true);

		Button[] delimiterButtons = dialog.delimiterRadioGroup.delimiterButtons;
		for (Button button : delimiterButtons) {
			button.setEnabled(true);
		}

		dialog.selectAllButton.setEnabled(true);
		dialog.selectNoneButton.setEnabled(true);
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
		dialog.fileNameTextField.setText(groupingParseSpecification.getDataSourcePath());
		dialog.fileNameTextField.setEnabled(false);

		dialog.groupingNameTextField
				.setText(groupingParseSpecification.getGroupingName());
		dialog.groupingNameTextField.setEnabled(true);

		dialog.categoryIDLabel.setText(rowIDCategory.getCategoryName());

		dialog.numHeaderRowsSpinner.setSelection(groupingParseSpecification
				.getNumberOfHeaderLines());
		dialog.numHeaderRowsSpinner.setEnabled(true);

		dialog.columnOfRowIDSpinner.setSelection(groupingParseSpecification
				.getColumnOfRowIds() + 1);
		dialog.columnOfRowIDSpinner.setEnabled(true);

		Button[] delimiterButtons = dialog.delimiterRadioGroup.delimiterButtons;
		boolean delimiterFound = false;
		for (int i = 0; i < delimiterButtons.length - 2; i++) {
			Button button = delimiterButtons[i];
			if (((String) button.getData()).equals(groupingParseSpecification
					.getDelimiter())) {
				button.setSelection(true);
				delimiterFound = true;
			}
			button.setEnabled(true);
		}
		if (!delimiterFound) {
			delimiterButtons[delimiterButtons.length - 1].setSelection(true);
			delimiterButtons[delimiterButtons.length - 1].setEnabled(true);
			dialog.delimiterRadioGroup.customizedDelimiterTextField.setEnabled(true);
		} else {
			dialog.delimiterRadioGroup.customizedDelimiterTextField.setEnabled(false);
		}

		dialog.selectAllButton.setEnabled(true);

		dialog.selectNoneButton.setEnabled(true);

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

		fillIDTypeCombo(rowIDCategory, rowIDTypes, dialog.rowIDCombo);
		dialog.rowIDCombo.setEnabled(true);
		dialog.rowIDCombo.select(dialog.rowIDCombo.indexOf(groupingParseSpecification
				.getRowIDSpecification().getIdType()));
	}

	private void initWidgetsWithDefaultValues() {

		dialog.fileNameTextField.setText("");
		dialog.fileNameTextField.setEnabled(false);

		dialog.groupingNameTextField.setText("");
		dialog.groupingNameTextField.setEnabled(false);

		dialog.categoryIDLabel.setText(rowIDCategory.getCategoryName());

		fillIDTypeCombo(rowIDCategory, rowIDTypes, dialog.rowIDCombo);
		dialog.rowIDCombo.setEnabled(false);

		dialog.numHeaderRowsSpinner.setSelection(1);
		dialog.numHeaderRowsSpinner.setEnabled(false);

		dialog.columnOfRowIDSpinner.setSelection(1);
		dialog.columnOfRowIDSpinner.setEnabled(false);

		Button[] delimiterButtons = dialog.delimiterRadioGroup.delimiterButtons;
		delimiterButtons[0].setSelection(true);
		for (Button button : delimiterButtons) {
			button.setEnabled(false);
		}
		dialog.delimiterRadioGroup.customizedDelimiterTextField.setEnabled(false);

		dialog.selectAllButton.setEnabled(false);

		dialog.selectNoneButton.setEnabled(false);

		dialog.showAllColumnsButton.setEnabled(false);
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void numHeaderRowsSpinnerModified() {
		try {
			int numHeaderRows = dialog.numHeaderRowsSpinner.getSelection();
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
	public void columnOfRowIDSpinnerModified() {
		groupingParseSpecification.setColumnOfRowIds(dialog.columnOfRowIDSpinner
				.getSelection() - 1);
		previewTableManager.updateTableColors(
				groupingParseSpecification.getNumberOfHeaderLines(), -1,
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

	/**
	 * Reloads the dataset using the delimiter specified by the
	 * customizedDelimiterTextField.
	 */
	public void customizedDelimiterTextFieldModified() {
		groupingParseSpecification
				.setDelimiter(dialog.delimiterRadioGroup.customizedDelimiterTextField
						.getText());
		createDataPreviewTableFromFile(true);
	}

	/**
	 * Reloads the dataset using the delimiter specified by the selected button.
	 * 
	 * @param selectedButton
	 */
	public void delimiterRadioButtonSelected(Button selectedButton) {
		if (selectedButton != dialog.delimiterRadioGroup.delimiterButtons[dialog.delimiterRadioGroup.delimiterButtons.length - 1]) {
			dialog.delimiterRadioGroup.customizedDelimiterTextField.setEnabled(false);
			groupingParseSpecification.setDelimiter((String) selectedButton.getData());
			createDataPreviewTableFromFile(true);
		} else {
			dialog.delimiterRadioGroup.customizedDelimiterTextField.setEnabled(true);
			groupingParseSpecification.setDelimiter(" ");
			createDataPreviewTableFromFile(true);
		}
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
						dialog.numHeaderRowsSpinner.setSelection(numHeaderRows);
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

		fillIDTypeCombo(rowIDCategory, rowIDTypes, dialog.rowIDCombo);
		if (mostProbableRecordIDType != null)
			dialog.rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));
	}

	protected void updateWidgetsAccordingToTableChanges() {
		dialog.columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		dialog.numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
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

	/**
	 * @param idCategory
	 * @param idTypes
	 * @param idTypeCombo
	 */
	private void fillIDTypeCombo(IDCategory idCategory, ArrayList<IDType> idTypes,
			Combo idTypeCombo) {

		ArrayList<IDType> allIDTypesOfCategory = new ArrayList<IDType>(
				idCategory.getIdTypes());

		// String previousSelection = null;
		//
		// if (idTypeCombo.getSelectionIndex() != -1) {
		// previousSelection =
		// idTypeCombo.getItem(idTypeCombo.getSelectionIndex());
		// }
		idTypeCombo.removeAll();
		idTypes.clear();

		for (IDType idType : allIDTypesOfCategory) {
			if (!idType.isInternalType()) {
				idTypes.add(idType);
				idTypeCombo.add(idType.getTypeName());
			}
		}

		// int selectionIndex = -1;
		// if (previousSelection != null) {
		// selectionIndex = idTypeCombo.indexOf(previousSelection);
		// }
		if (idTypes.size() == 1) {
			idTypeCombo.setText(idTypeCombo.getItem(0));
			idTypeCombo.select(0);
		} else {
//			idTypeCombo.setText("<Please Select>");
			idTypeCombo.deselectAll();
		}
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
