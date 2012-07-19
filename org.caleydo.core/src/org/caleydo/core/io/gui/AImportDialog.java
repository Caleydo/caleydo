/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract base class for data import dialogs that are used to load tabular
 * data.
 * 
 * @author Christian Partl
 * 
 */
public abstract class AImportDialog extends Dialog {

	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	protected static int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * Maximum number of previewed columns in {@link #previewTable}.
	 */
	protected static int MAX_PREVIEW_TABLE_COLUMNS = 10;

	/**
	 * The maximum number of ids that are tested in order to determine the
	 * {@link IDType}.
	 */
	protected static int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Textfield for the input file name.
	 */
	protected Text fileNameTextField;

	/**
	 * File name of the input file.
	 */
	protected String inputFileName = "";

	/**
	 * Table that displays a preview of the data of the file specified by
	 * {@link #inputFileName}.
	 */
	protected Table previewTable;

	/**
	 * The current row id category.
	 */
	protected IDCategory rowIDCategory;

	/**
	 * The current column id category.
	 */
	protected IDCategory columnIDCategory;

	/**
	 * Combo box to specify the row ID Type.
	 */
	protected Combo rowIDCombo;

	/**
	 * Combo box to specify the column ID Type.
	 */
	protected Combo columnIDCombo;

	/**
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	protected ArrayList<IDType> rowIDTypes;

	/**
	 * The IDTypes available for {@link #columnIDCategory}.
	 */
	protected ArrayList<IDType> columnIDTypes;

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
	 * {@link MatrixDefinition} of the dataset that shall be loaded.
	 */
	protected MatrixDefinition matrixDefinition;

	/**
	 * Spinner used to define the index of the row that contains the column ids.
	 */
	protected Spinner rowOfColumnIDSpinner;

	/**
	 * Spinner used to define the index of the column that contains the row ids.
	 */
	protected Spinner columnOfRowIDSpinner;

	/**
	 * Spinner used to define the index of the row from where on data is
	 * contained.
	 */
	protected Spinner numHeaderRowsSpinner;

	/**
	 * Spinner used to define the index of the column from where on data is
	 * contained.
	 */
	protected Spinner dataStartColumnSpinner;

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and
	 * all columns of the data file.
	 */
	protected String[][] dataMatrix = new String[MAX_PREVIEW_TABLE_ROWS][];

	/**
	 * The total number of columns of the input file.
	 */
	protected int totalNumberOfColumns;

	/**
	 * The total number of rows of the input file.
	 */
	protected int totalNumberOfRows;

	/**
	 * Button to specify whether all columns of the data file should be shown in
	 * the {@link #previewTable}.
	 */
	protected Button showAllColumnsButton;

	/**
	 * Determines whether all columns of the data file shall be shown in the
	 * {@link #previewTable}.
	 */
	protected boolean showAllColumns = false;

	/**
	 * Shows the total number columns in the data file and the number of
	 * displayed columns of the {@link #previewTable}.
	 */
	protected Label tableInfoLabel;

	/**
	 * @param parent
	 */
	public AImportDialog(Shell parent) {
		super(parent);
		matrixDefinition = createConcreteMatrixDefinition();
	}

	/**
	 * Implementors are intended to create the concrete {@link MatrixDefinition}
	 * instance in this method.
	 * 
	 * @return The instance of {@link MatrixDefinition} used by subclasses.
	 */
	protected abstract MatrixDefinition createConcreteMatrixDefinition();

	// protected void createRowIDTypeGroup(Composite parent) {
	// Group idTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// idTypeGroup.setText("Row ID type");
	// idTypeGroup.setLayout(new RowLayout());
	// idTypeGroup.setLayoutData(new GridData(SWT.LEFT));
	// rowIDCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
	// rowIDTypes = new ArrayList<IDType>();
	//
	// fillRowIDTypeCombo();
	//
	// rowIDCombo.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// TableColumn idColumn = previewTable.getColumn(1);
	// idColumn.setText(rowIDCombo.getText());
	// }
	// });
	// }
	//
	// protected void fillRowIDTypeCombo() {
	// ArrayList<IDType> tempIDTypes = rowIDCategory.getIdTypes();
	//
	// rowIDTypes.clear();
	// for (IDType idType : tempIDTypes) {
	// if (!idType.isInternalType())
	// rowIDTypes.add(idType);
	// }
	//
	// String[] idTypesAsString = new String[rowIDTypes.size()];
	// int index = 0;
	// for (IDType idType : rowIDTypes) {
	// idTypesAsString[index] = idType.getTypeName();
	// index++;
	// }
	//
	// rowIDCombo.setItems(idTypesAsString);
	// rowIDCombo.setEnabled(true);
	// rowIDCombo.select(0);
	//
	// }

	protected void createIDTypeGroup(Composite parent, boolean isColumnIDTypeGroup) {
		Group idTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		idTypeGroup.setText(isColumnIDTypeGroup ? "Column ID type" : "Row ID type");
		idTypeGroup.setLayout(new RowLayout());
		idTypeGroup.setLayoutData(new GridData(SWT.LEFT));
		Combo idCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
		ArrayList<IDType> idTypes = new ArrayList<IDType>();

		if (isColumnIDTypeGroup) {
			columnIDCombo = idCombo;
			columnIDTypes = idTypes;
		} else {
			rowIDCombo = idCombo;
			rowIDTypes = idTypes;
		}

		fillIDTypeCombo(isColumnIDTypeGroup ? columnIDCategory : rowIDCategory, idTypes,
				idCombo);

		if (!isColumnIDTypeGroup) {
			rowIDCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableColumn idColumn = previewTable.getColumn(1);
					idColumn.setText(rowIDCombo.getText());
				}
			});
		}
	}

	protected void fillIDTypeCombo(IDCategory idCategory, ArrayList<IDType> idTypes,
			Combo idTypeCombo) {

		if (idCategory == null) {
			idTypeCombo.setEnabled(false);
			return;
		}

		ArrayList<IDType> tempIDTypes = idCategory.getIdTypes();

		String previousSelection = null;
		if (idTypeCombo.getSelectionIndex() != -1) {
			previousSelection = idTypeCombo.getItem(idTypeCombo.getSelectionIndex());
		}
		idTypeCombo.removeAll();

		idTypes.clear();
		for (IDType idType : tempIDTypes) {
			if (!idType.isInternalType())
				idTypes.add(idType);
		}

		for (IDType idType : idTypes) {
			idTypeCombo.add(idType.getTypeName());
		}

		int selectionIndex = -1;
		if (previousSelection != null) {
			selectionIndex = idTypeCombo.indexOf(previousSelection);
		}
		if (selectionIndex == -1) {
			idTypeCombo.setText("<Please select>");
			idTypeCombo.clearSelection();
		} else {
			idTypeCombo.setText(idTypeCombo.getItem(selectionIndex));
			idTypeCombo.select(selectionIndex);
		}

		idTypeCombo.setEnabled(true);
		// idTypeCombo.select(0);

	}

	protected void createDelimiterGroup(Composite parent) {
		Group delimiterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setText("Separated by (delimiter)");
		delimiterGroup.setLayout(new RowLayout());

		final Button[] delimiterButtons = new Button[6];

		delimiterButtons[0] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[0].setSelection(true);
		delimiterButtons[0].setText("TAB");
		delimiterButtons[0].setData("\t");
		delimiterButtons[0].setBounds(10, 5, 75, 30);

		delimiterButtons[1] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[1].setText(";");
		delimiterButtons[1].setData(";");
		delimiterButtons[1].setBounds(10, 30, 75, 30);

		delimiterButtons[2] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[2].setText(",");
		delimiterButtons[2].setData(",");
		delimiterButtons[2].setBounds(10, 55, 75, 30);

		delimiterButtons[3] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[3].setText(".");
		delimiterButtons[3].setData(".");
		delimiterButtons[3].setBounds(10, 55, 75, 30);

		delimiterButtons[4] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[4].setText("SPACE");
		delimiterButtons[4].setData(" ");
		delimiterButtons[4].setBounds(10, 55, 75, 30);

		delimiterButtons[5] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[5].setText("Other");
		delimiterButtons[5].setBounds(10, 55, 75, 30);

		final Text customizedDelimiterTextField = new Text(delimiterGroup, SWT.BORDER);
		customizedDelimiterTextField.setBounds(0, 0, 75, 30);
		customizedDelimiterTextField.setTextLimit(1);
		customizedDelimiterTextField.setEnabled(false);
		customizedDelimiterTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				matrixDefinition.setDelimiter(customizedDelimiterTextField.getText());
				createDataPreviewTableFromFile();
				// composite.pack();
			}

		});

		SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				if (selectedButton != delimiterButtons[5]) {
					customizedDelimiterTextField.setEnabled(false);
					matrixDefinition.setDelimiter((String) selectedButton.getData());
					createDataPreviewTableFromFile();
				} else {
					customizedDelimiterTextField.setEnabled(true);
					matrixDefinition.setDelimiter(" ");
					createDataPreviewTableFromFile();
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}

	/**
	 * Creates the {@link #previewTable} according to the {@link #dataMatrix}.
	 */
	protected void createDataPreviewTableFromDataMatrix() {

		if (dataMatrix[0] == null)
			return;

		previewTable.removeAll();
		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		int numTableColumns = showAllColumns ? dataMatrix[0].length + 1 : Math.min(
				dataMatrix[0].length + 1, MAX_PREVIEW_TABLE_COLUMNS + 1);

		for (int i = 0; i < numTableColumns; i++) {
			TableColumn column = new TableColumn(previewTable, SWT.NONE);
			column.setWidth(100);
		}

		createUseColumnRow();

		for (int i = 0; i < dataMatrix.length; i++) {
			String[] dataRow = dataMatrix[i];
			TableItem item = new TableItem(previewTable, SWT.NONE);
			item.setText(0, "" + i + 1);
			for (int j = 0; j < numTableColumns - 1; j++) {
				item.setText(j + 1, dataRow[j]);
			}
		}

		determineRowIDType();
		updateTableColors();
		updateTableInfoLabel();

	}

	/**
	 * Creates a composite that contains the {@link #tableInfoLabel} and the
	 * {@link #showAllColumnsButton}.
	 * 
	 * @param parent
	 */
	protected void createTableInfo(Composite parent) {
		Composite tableInfoComposite = new Composite(parent, SWT.NONE);
		tableInfoComposite.setLayout(new GridLayout(4, false));

		tableInfoLabel = new Label(tableInfoComposite, SWT.NONE);

		Label separator = new Label(tableInfoComposite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData separatorGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		separatorGridData.heightHint = 16;
		separator.setLayoutData(separatorGridData);
		showAllColumnsButton = new Button(tableInfoComposite, SWT.CHECK);
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(false);
		showAllColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showAllColumns = showAllColumnsButton.getSelection();
				createDataPreviewTableFromDataMatrix();
			}

		});

		Label showAllColumnsLabel = new Label(tableInfoComposite, SWT.NONE);
		showAllColumnsLabel.setText("Show all columns");
	}

	/**
	 * Creates the {@link #previewTable} according to the data file. The
	 * {@link #dataMatrix} is also created in this method.
	 */
	protected void createDataPreviewTableFromFile() {

		String delimiter = matrixDefinition.getDelimiter();

		// Clear table if not empty
		previewTable.removeAll();

		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		for (int i = 0; i < dataMatrix.length; i++) {
			dataMatrix[i] = null;
		}

		// final TableEditor editor = new TableEditor(previewTable);
		// editor.horizontalAlignment = SWT.LEFT;
		// editor.grabHorizontal = true;

		// Read preview table
		BufferedReader file;
		try {
			file = GeneralManager.get().getResourceLoader()
					.getResource(matrixDefinition.getDataSourcePath());

			String line = "";
			String nextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			// TableItem item;
			// int colIndex = 0;
			totalNumberOfColumns = 0;

			// Read labels
			if ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, delimiter, false);
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);
				column.setText("");

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					if (totalNumberOfColumns < MAX_PREVIEW_TABLE_COLUMNS) {
						final TableColumn dataColumn = new TableColumn(previewTable,
								SWT.NONE);
						dataColumn.setWidth(100);
						dataColumn.setText(nextToken);
					}
					totalNumberOfColumns++;
				}

			}

			totalNumberOfRows = 0;

			createUseColumnRow();
			readDataRow(line, delimiter, totalNumberOfRows);
			totalNumberOfRows++;

			// Read raw data
			while ((line = file.readLine()) != null
					&& totalNumberOfRows < MAX_PREVIEW_TABLE_ROWS) {
				readDataRow(line, delimiter, totalNumberOfRows);
				totalNumberOfRows++;
			}

			// check for experiment cluster info in the rest of the file
			while ((line = file.readLine()) != null) {

				totalNumberOfRows++;

				tokenizer = new StringTokenizer(line, delimiter, true);

				if (!tokenizer.hasMoreTokens())
					continue;

				nextToken = tokenizer.nextToken();
			}

		} catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found!");
		} catch (IOException ioe) {
			throw new IllegalStateException("Input/output problem!");
		}

		determineRowIDType();
		updateTableColors();
		updateTableInfoLabel();

		parentComposite.pack();

		previewTableCreatedFromFile();
	}

	private void readDataRow(String line, String delimiter, int rowIndex) {
		// last flag triggers return of delimiter itself
		StringTokenizer tokenizer = new StringTokenizer(line, delimiter, true);
		TableItem item = new TableItem(previewTable, SWT.NONE);
		item.setText("" + (rowIndex + 1)); // +1 to be intuitive for
		// a non programmer :)
		int colIndex = 0;
		boolean isCellFilled = false;

		String[] dataRow = new String[totalNumberOfColumns];
		dataMatrix[rowIndex] = dataRow;

		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();

			// Check for empty cells
			if (nextToken.equals(delimiter) && !isCellFilled) {
				dataRow[colIndex] = "";
				if (colIndex + 1 < previewTable.getColumnCount()) {
					item.setText(colIndex + 1, dataRow[colIndex]);
				}
				colIndex++;
			} else if (nextToken.equals(delimiter) && isCellFilled) {
				isCellFilled = false; // reset
			} else {
				isCellFilled = true;
				dataRow[colIndex] = nextToken;
				if (colIndex + 1 < previewTable.getColumnCount()) {
					item.setText(colIndex + 1, dataRow[colIndex]);
				}
				colIndex++;
			}
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
					boolean bSkipColumn = false;

					for (TableItem item : previewTable.getItems()) {
						bSkipColumn = !((Button) e.widget).getSelection();

						if (bSkipColumn) {
							textColor = Display.getCurrent().getSystemColor(
									SWT.COLOR_GRAY);
						} else {
							textColor = Display.getCurrent().getSystemColor(
									SWT.COLOR_BLACK);
						}

						item.setForeground(((Integer) e.widget.getData("column")),
								textColor);
					}
				}
			});

			selectedColumnButtons.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, colIndex);
			tableEditors.add(editor);
		}
	}

	private void determineRowIDType() {

		ArrayList<IDCategory> idCategories = getAvailableIDCategories();

		TableItem[] items = previewTable.getItems();
		ArrayList<String> idList = new ArrayList<String>();
		int rowIndex = 1;
		while (rowIndex < items.length
				&& rowIndex <= MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION) {
			idList.add(items[rowIndex].getText(matrixDefinition.getColumnOfRowIds() + 1));
			rowIndex++;
		}

		int maxCorrectElements = 0;
		IDType mostProbableIDType = null;

		for (IDCategory idCategory : idCategories) {

			rowIDTypes = new ArrayList<IDType>();
			ArrayList<IDType> alIDTypesTemp = idCategory.getIdTypes();
			for (IDType idType : alIDTypesTemp) {
				if (!idType.isInternalType())
					rowIDTypes.add(idType);
			}

			IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(idCategory);

			for (IDType idType : rowIDTypes) {

				int currentCorrectElements = 0;

				for (String currentID : idList) {

					if (idType.getColumnType().equals(EDataType.INT)) {
						try {
							Integer idInt = Integer.valueOf(currentID);
							if (idMappingManager.doesElementExist(idType, idInt)) {
								currentCorrectElements++;
							}
						} catch (NumberFormatException e) {
						}
					} else if (idType.getColumnType().equals(EDataType.STRING)) {
						if (idMappingManager.doesElementExist(idType, currentID)) {
							currentCorrectElements++;
						} else if (idType.getTypeName().equals("REFSEQ_MRNA")) {
							if (currentID.contains(".")) {
								if (idMappingManager.doesElementExist(idType,
										currentID.substring(0, currentID.indexOf(".")))) {
									currentCorrectElements++;
								}
							}
						}
					}

					if (currentCorrectElements >= idList.size()) {

						setMostProbableRecordIDType(mostProbableIDType);

						return;
					}
					if (currentCorrectElements >= maxCorrectElements) {
						maxCorrectElements = currentCorrectElements;
						mostProbableIDType = idType;
					}
				}
			}
		}
		setMostProbableRecordIDType(mostProbableIDType);
	}

	/**
	 * @return List of IDCategories that are considered by the concrete
	 *         implementation of the dialog.
	 */
	protected abstract ArrayList<IDCategory> getAvailableIDCategories();

	/**
	 * This method is called when the most probable record id type according to
	 * the data was determined.
	 * 
	 * @param mostProbableIDType
	 *            The most probable id type. Null if no id type could be
	 *            determined.
	 */
	protected abstract void setMostProbableRecordIDType(IDType mostProbableIDType);

	/**
	 * Updates the color of table rows and columns according to their properties
	 * (id, header, data).
	 */
	protected void updateTableColors() {

//		colorTableRow(0, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
//		colorTableColumn(0, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));

		int oldNumHeaderLines = matrixDefinition.getNumberOfHeaderLines();
		for (int i = 1; i < oldNumHeaderLines + 1; i++) {
			colorTableRow(i, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}

		if (allowsColumnIDs()) {
			int oldIDRowIndex = matrixDefinition.getRowOfColumnIDs() + 1;
			if (oldNumHeaderLines <= oldIDRowIndex)
				colorTableRow(oldIDRowIndex,
						Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}

		int oldIDColumnIndex = matrixDefinition.getColumnOfRowIds() + 1;
		colorTableColumn(oldIDColumnIndex,
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		selectedColumnButtons.get(oldIDColumnIndex - 1).setVisible(true);

		int numHeaderLines = numHeaderRowsSpinner.getSelection();
		if (numHeaderLines < previewTable.getItemCount()) {
			matrixDefinition.setNumberOfHeaderLines(numHeaderLines);
			for (int i = 1; i < numHeaderLines + 1; i++) {
				colorTableRow(i, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
		}

		if (allowsColumnIDs()) {
			int idRowIndex = rowOfColumnIDSpinner.getSelection();
			if (idRowIndex < previewTable.getItemCount()) {
				matrixDefinition.setRowOfColumnIDs(idRowIndex - 1);
				colorTableRow(idRowIndex,
						Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
			}
		}
		int idColumnIndex = columnOfRowIDSpinner.getSelection();
		if (idColumnIndex < previewTable.getColumnCount()) {
			matrixDefinition.setColumnOfRowIds(idColumnIndex - 1);
			colorTableColumn(idColumnIndex,
					Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
			selectedColumnButtons.get(idColumnIndex - 1).setVisible(false);
		}

	}

	private void colorTableRow(int rowIndex, Color color) {
		TableItem item = previewTable.getItem(rowIndex);
		for (int i = 0; i < previewTable.getColumnCount(); i++) {
			item.setBackground(i, color);
		}
	}

	private void colorTableColumn(int columnIndex, Color color) {
		for (int i = 1; i < previewTable.getItemCount(); i++) {
			previewTable.getItem(i).setBackground(columnIndex, color);
		}
	}

	/**
	 * @return True, if the concrete dialog implementation allows the
	 *         specification of column IDs.
	 */
	protected abstract boolean allowsColumnIDs();

	/**
	 * Method that is called after the {@link #previewTable} was updated.
	 */
	protected abstract void previewTableCreatedFromFile();

	protected void updateTableInfoLabel() {
		tableInfoLabel.setText((previewTable.getColumnCount() - 1) + " of "
				+ totalNumberOfColumns + " columns shown");
	}

}
