/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.File;
import java.util.ArrayList;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian Partl
 * 
 */
public class LoadDataSetPage extends AImportDataPage implements Listener {

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
	 * Text field for the name of the dataset.
	 */
	private Text dataSetLabelTextField;

	/**
	 * Button to specify whether the dataset is homogeneous, i.e. all columns
	 * have the same scale.
	 */
	private Button buttonHomogeneous;

	/**
	 * Combo box to specify the {@link IDCategory} for the columns of the
	 * dataset.
	 */
	private Combo columnIDCategoryCombo;
	/**
	 * Combo box to specify the {@link IDCategory} for the rows of the dataset.
	 */
	private Combo rowIDCategoryCombo;

	/**
	 * Default path of the open dataset dialog.
	 */
	private String filePath = "";

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
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	/**
	 * All registered id categories.
	 */
	private ArrayList<IDCategory> registeredIDCategories;

	/**
	 * Manager for {@link #previewTable} that extends its features.
	 */
	private PreviewTableManager previewTableManager;

	/**
	 * @param pageName
	 */
	protected LoadDataSetPage(String pageName, DataSetDescription dataSetDescription) {
		super(pageName, dataSetDescription);
		inputFileName = dataSetDescription.getDataSourcePath();
		if (inputFileName == null)
			inputFileName = "";
	}

	@Override
	public void createControl(Composite parent) {

		dataSetDescription.setDelimiter("\t");
		dataSetDescription.setNumberOfHeaderLines(1);
		dataSetDescription.setRowOfColumnIDs(0);
		dataSetDescription.setColumnOfRowIds(0);
		registeredIDCategories = new ArrayList<IDCategory>();
		registeredIDCategories.addAll(IDCategory.getAllRegisteredIDCategories());

		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, true);
		parentComposite.setLayout(layout);

		// File Selection

		createFileSelectionPart(parentComposite);

		// Dataset Name

		createDataSetNamePart(parentComposite);

		// Delimiters

		Composite delimiterComposite = new Composite(parentComposite, SWT.NONE);
		delimiterComposite.setLayout(new GridLayout(1, false));
		delimiterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));
		createDelimiterGroup(delimiterComposite);

		// Row Config

		createRowConfigPart(parentComposite);

		// Column Config

		createColumnConfigPart(parentComposite);

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		// previewTable.setHeaderVisible(true);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = numGridCols;
		gridData.heightHint = 400;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		createTableInfo(parentComposite);

		previewTableManager = new PreviewTableManager(previewTable);

		// Check if an external file name is given to the action
		if (!inputFileName.isEmpty()) {
			fileNameTextField.setText(inputFileName);
			dataSetDescription.setDataSourcePath(inputFileName);
			// mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTableFromFile();
		}
		setControl(parentComposite);
	}

	private void createRowConfigPart(Composite parent) {

		Group rowConfigGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		rowConfigGroup.setText("Row configuration");
		rowConfigGroup.setLayout(new GridLayout(2, false));
		rowConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite leftConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createIDCategoryGroup(leftConfigGroupPart, "Row ID category", false);
		createIDTypeGroup(leftConfigGroupPart, false);

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of header rows");

		numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(1);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		numHeaderRowsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {

					int numHeaderRows = numHeaderRowsSpinner.getSelection();
					int idRowIndex = rowOfColumnIDSpinner.getSelection();
					if (idRowIndex > numHeaderRows) {
						rowOfColumnIDSpinner.setSelection(numHeaderRows);
						dataSetDescription.setRowOfColumnIDs(numHeaderRows - 1);
					}
					dataSetDescription.setNumberOfHeaderLines(numHeaderRows);
					previewTableManager.updateTableColors(
							dataSetDescription.getNumberOfHeaderLines(),
							dataSetDescription.getRowOfColumnIDs() + 1,
							dataSetDescription.getColumnOfRowIds() + 1);

				} catch (NumberFormatException exc) {

				}

			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with row IDs");
		// columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		columnOfRowIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnOfRowIDSpinner.setMinimum(1);
		columnOfRowIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnOfRowIDSpinner.setIncrement(1);
		columnOfRowIDSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		columnOfRowIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dataSetDescription.setColumnOfRowIds(columnOfRowIDSpinner.getSelection() - 1);
				previewTableManager.updateTableColors(
						dataSetDescription.getNumberOfHeaderLines(),
						dataSetDescription.getRowOfColumnIDs() + 1,
						dataSetDescription.getColumnOfRowIds() + 1);
			}
		});

		Composite righttConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		righttConfigGroupPart.setLayout(new GridLayout(1, false));
		righttConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createNewIDCategoryButton(righttConfigGroupPart);
		createNewIDTypeButton(righttConfigGroupPart);
	}

	private void createNewIDCategoryButton(Composite parent) {
		Button createIDCategoryButton = new Button(parent, SWT.PUSH);
		createIDCategoryButton.setText("New");
		createIDCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateIDCategoryDialog dialog = new CreateIDCategoryDialog(new Shell());
				int status = dialog.open();

				if (status == Dialog.OK) {
					registeredIDCategories = new ArrayList<IDCategory>();
					registeredIDCategories.addAll(IDCategory
							.getAllRegisteredIDCategories());
					fillIDCategoryCombo(rowIDCategoryCombo);
					fillIDCategoryCombo(columnIDCategoryCombo);
				}

				super.widgetSelected(e);
			}
		});
	}

	private void createNewIDTypeButton(Composite parent) {
		Button createIDTypeButton = new Button(parent, SWT.PUSH);
		createIDTypeButton.setText("New");
		createIDTypeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell());
				int status = dialog.open();

				if (status == Dialog.OK) {

					updateIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);

					updateIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);
				}

				super.widgetSelected(e);
			}
		});
	}

	private void createColumnConfigPart(Composite parent) {

		Group columnConfigGroup = new Group(parent, SWT.NONE);
		columnConfigGroup.setText("Column configuration");
		columnConfigGroup.setLayout(new GridLayout(2, false));
		columnConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite leftConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createIDCategoryGroup(leftConfigGroupPart, "Column ID category", true);
		createIDTypeGroup(leftConfigGroupPart, true);

		Label rowOfColumnIDLabel = new Label(leftConfigGroupPart, SWT.NONE);
		rowOfColumnIDLabel.setText("Row with column IDs");

		rowOfColumnIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		rowOfColumnIDSpinner.setMinimum(1);
		rowOfColumnIDSpinner.setMaximum(Integer.MAX_VALUE);
		rowOfColumnIDSpinner.setIncrement(1);
		rowOfColumnIDSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rowOfColumnIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int numHeaderRows = numHeaderRowsSpinner.getSelection();
				int idRowIndex = rowOfColumnIDSpinner.getSelection();
				dataSetDescription.setRowOfColumnIDs(idRowIndex - 1);
				if (idRowIndex > numHeaderRows) {
					numHeaderRowsSpinner.setSelection(idRowIndex);
					dataSetDescription.setNumberOfHeaderLines(idRowIndex);
				}
				previewTableManager.updateTableColors(
						dataSetDescription.getNumberOfHeaderLines(),
						dataSetDescription.getRowOfColumnIDs() + 1,
						dataSetDescription.getColumnOfRowIds() + 1);
			}
		});

		createDataPropertiesGroup(leftConfigGroupPart);

		Composite righttConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		righttConfigGroupPart.setLayout(new GridLayout(1, false));
		righttConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createNewIDCategoryButton(righttConfigGroupPart);
		createNewIDTypeButton(righttConfigGroupPart);
	}

	private void createFileSelectionPart(Composite parent) {

		Group inputFileGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		inputFileGroup.setLayoutData(new GridData(SWT.BEGINNING));

		Button buttonFileChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonFileChooser.setText("Choose data file...");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 300;
		fileNameTextField.setLayoutData(gridData);
		fileNameTextField.setEnabled(false);
		fileNameTextField.addListener(SWT.Modify, this);

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv;*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				inputFileName = fileDialog.open();

				if (inputFileName == null)
					return;

				dataSetDescription.setDataSourcePath(inputFileName);
				fileNameTextField.setText(inputFileName);

				dataSetLabelTextField.setText(determineDataSetLabel());
				columnIDCategoryCombo.setEnabled(true);
				rowIDCategoryCombo.setEnabled(true);

				createDataPreviewTableFromFile();
			}
		});
	}

	private void createDataSetNamePart(Composite parent) {
		Group dataSetLabelGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		dataSetLabelGroup.setText("Dataset name");
		dataSetLabelGroup.setLayout(new GridLayout(1, false));
		dataSetLabelGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dataSetLabelTextField = new Text(dataSetLabelGroup, SWT.BORDER);
		dataSetLabelTextField.setText(determineDataSetLabel());
		dataSetLabelTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
				dataSetDescription.setDelimiter(customizedDelimiterTextField.getText());
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
					dataSetDescription.setDelimiter((String) selectedButton.getData());
					createDataPreviewTableFromFile();
				} else {
					customizedDelimiterTextField.setEnabled(true);
					dataSetDescription.setDelimiter(" ");
					createDataPreviewTableFromFile();
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}

	private void createIDTypeGroup(Composite parent, boolean isColumnIDTypeGroup) {
		Label idTypeLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText(isColumnIDTypeGroup ? "Column ID type" : "Row ID type");
		// idTypeLabel.setLayout(new RowLayout());
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo idCombo = new Combo(parent, SWT.DROP_DOWN);
		ArrayList<IDType> idTypes = new ArrayList<IDType>();

		if (isColumnIDTypeGroup) {
			columnIDCombo = idCombo;
			columnIDTypes = idTypes;
		} else {
			rowIDCombo = idCombo;
			rowIDTypes = idTypes;
		}

		updateIDTypeCombo(isColumnIDTypeGroup ? columnIDCategory : rowIDCategory,
				idTypes, idCombo);

		idCombo.addListener(SWT.Modify, this);
	}

	private void updateIDTypeCombo(IDCategory idCategory, ArrayList<IDType> idTypes,
			Combo idTypeCombo) {
		if (idCategory != null) {
			ArrayList<IDType> allIDTypesOfCategory = new ArrayList<IDType>(
					idCategory.getIdTypes());

			String previousSelection = null;

			if (idTypeCombo.getSelectionIndex() != -1) {
				previousSelection = idTypeCombo.getItem(idTypeCombo.getSelectionIndex());
			}
			idTypeCombo.removeAll();
			idTypes.clear();

			for (IDType idType : allIDTypesOfCategory) {
				if (!idType.isInternalType()) {
					idTypes.add(idType);
					idTypeCombo.add(idType.getTypeName());
				}
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
		} else {
			idTypeCombo.setEnabled(false);
		}
	}

	private void createDataPreviewTableFromFile() {
		parser.parse(inputFileName, dataSetDescription.getDelimiter(), false,
				MAX_PREVIEW_TABLE_ROWS);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		totalNumberOfRows = parser.getTotalNumberOfRows();
		previewTableManager.createDataPreviewTableFromDataMatrix(dataMatrix,
				MAX_PREVIEW_TABLE_COLUMNS);
		selectedColumnButtons = previewTableManager.getSelectedColumnButtons();
		determineRowIDType();
		previewTableManager.updateTableColors(
				dataSetDescription.getNumberOfHeaderLines(),
				dataSetDescription.getColumnOfRowIds() + 1,
				dataSetDescription.getRowOfColumnIDs() + 1);
		updateWidgetsAccordingToTableChanges();

		parentComposite.pack();
	}

	private void determineRowIDType() {

		ArrayList<IDCategory> idCategories = getAvailableIDCategories();

		TableItem[] items = previewTable.getItems();
		ArrayList<String> idList = new ArrayList<String>();
		int rowIndex = 1;
		while (rowIndex < items.length
				&& rowIndex <= MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION) {
			idList.add(items[rowIndex].getText(dataSetDescription.getColumnOfRowIds() + 1));
			rowIndex++;
		}

		int maxCorrectElements = 0;
		IDType mostProbableIDType = null;

		for (IDCategory idCategory : idCategories) {

			ArrayList<IDType> alIDTypesTemp = idCategory.getIdTypes();
			rowIDTypes = new ArrayList<IDType>(alIDTypesTemp.size());
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
				previewTableManager
						.createDataPreviewTableFromDataMatrix(dataMatrix,
								showAllColumns ? totalNumberOfColumns
										: MAX_PREVIEW_TABLE_COLUMNS);
				selectedColumnButtons = previewTableManager.getSelectedColumnButtons();
				determineRowIDType();
				previewTableManager.updateTableColors(
						dataSetDescription.getNumberOfHeaderLines(),
						dataSetDescription.getRowOfColumnIDs() + 1,
						dataSetDescription.getColumnOfRowIds() + 1);
				updateWidgetsAccordingToTableChanges();
				showAllColumnsButton.setSelection(showAllColumns);
			}

		});

		Label showAllColumnsLabel = new Label(tableInfoComposite, SWT.NONE);
		showAllColumnsLabel.setText("Show all columns");
	}

	private String determineDataSetLabel() {

		if (inputFileName == null || inputFileName.isEmpty())
			return "<Insert data set name>";

		return inputFileName.substring(inputFileName.lastIndexOf(File.separator) + 1,
				inputFileName.lastIndexOf("."));
	}

	private void createIDCategoryGroup(Composite parent, String groupLabel,
			final boolean isColumnCategory) {
		Label recordIDCategoryGroup = new Label(parent, SWT.SHADOW_ETCHED_IN);
		recordIDCategoryGroup.setText(groupLabel);
		// recordIDCategoryGroup.setLayout(new RowLayout());
		recordIDCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
		Combo idCategoryCombo = new Combo(parent, SWT.DROP_DOWN);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		idCategoryCombo.setText("<Please select>");

		if (isColumnCategory) {
			columnIDCategoryCombo = idCategoryCombo;
		} else {
			rowIDCategoryCombo = idCategoryCombo;
		}

		// int index = 0;
		fillIDCategoryCombo(idCategoryCombo);

		// idCategoryCombo.setEnabled(false);
		// idCategoryCombo.deselect(0);
		idCategoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isColumnCategory) {
					columnIDCategory = IDCategory.getIDCategory(columnIDCategoryCombo
							.getItem(columnIDCategoryCombo.getSelectionIndex()));
					updateIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);
				} else {
					rowIDCategory = IDCategory.getIDCategory(rowIDCategoryCombo
							.getItem(rowIDCategoryCombo.getSelectionIndex()));
					updateIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
				}

			}
		});
	}

	private void fillIDCategoryCombo(Combo idCategoryCombo) {

		String previousSelection = null;
		if (idCategoryCombo.getSelectionIndex() != -1) {
			previousSelection = idCategoryCombo.getItem(idCategoryCombo
					.getSelectionIndex());
		}

		idCategoryCombo.removeAll();
		for (IDCategory idCategory : registeredIDCategories) {
			idCategoryCombo.add(idCategory.getCategoryName());
		}

		int selectionIndex = -1;
		if (previousSelection != null) {
			selectionIndex = idCategoryCombo.indexOf(previousSelection);
		}
		if (selectionIndex == -1) {
			idCategoryCombo.setText("<Please select>");
			idCategoryCombo.clearSelection();
		} else {
			idCategoryCombo.setText(idCategoryCombo.getItem(selectionIndex));
			idCategoryCombo.select(selectionIndex);
		}
	}

	private void createDataPropertiesGroup(Composite parent) {
		// Group dataPropertiesGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		// dataPropertiesGroup.setText("Column properties");
		// dataPropertiesGroup.setLayout(new RowLayout());
		// dataPropertiesGroup.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		buttonHomogeneous = new Button(parent, SWT.CHECK);
		buttonHomogeneous.setText("Columns use same scale");
		buttonHomogeneous.setEnabled(true);
		buttonHomogeneous.setSelection(true);
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	@Override
	public void fillDataSetDescription() {

		IDSpecification rowIDSpecification = new IDSpecification();
		IDType rowIDType = rowIDTypes.get(rowIDCombo.getSelectionIndex());
		rowIDSpecification.setIdType(rowIDType.toString());
		if (rowIDType.getIDCategory().getCategoryName().equals("GENE"))
			rowIDSpecification.setIDTypeGene(true);
		rowIDSpecification.setIdCategory(rowIDType.getIDCategory().toString());
		if (rowIDType.getTypeName().equalsIgnoreCase("REFSEQ_MRNA")) {
			// for REFSEQ_MRNA we ignore the .1, etc.
			IDTypeParsingRules parsingRules = new IDTypeParsingRules();
			parsingRules.setSubStringExpression("\\.");
			rowIDSpecification.setIdTypeParsingRules(parsingRules);
		}

		IDSpecification columnIDSpecification = new IDSpecification();
		IDType columnIDType = columnIDTypes.get(columnIDCombo.getSelectionIndex());
		columnIDSpecification.setIdType(columnIDType.toString());
		if (columnIDType.getIDCategory().getCategoryName().equals("GENE"))
			columnIDSpecification.setIDTypeGene(true);
		columnIDSpecification.setIdCategory(columnIDType.getIDCategory().toString());

		dataSetDescription.setColumnIDSpecification(columnIDSpecification);
		dataSetDescription.setRowIDSpecification(rowIDSpecification);

		dataSetDescription.setDataHomogeneous(buttonHomogeneous.getSelection());
		dataSetDescription.setDataSetName(dataSetLabelTextField.getText());

		readDimensionDefinition();
	}

	/**
	 * prepares the dimension creation definition from the preview table. The
	 * dimension creation definition consists of the definition which columns in
	 * the data-CSV-file should be read, which should be skipped and the
	 * dimension-labels.
	 * 
	 * @return <code>true</code> if the preparation was successful,
	 *         <code>false</code> otherwise
	 */
	private void readDimensionDefinition() {
		ArrayList<String> dimensionLabels = new ArrayList<String>();

		ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
		// inputPattern = new StringBuffer("SKIP" + ";");

		// the columnIndex here is the columnIndex of the previewTable. This is
		// different by one from the index in the source csv.
		for (int columnIndex = 0; columnIndex < totalNumberOfColumns; columnIndex++) {

			if (dataSetDescription.getColumnOfRowIds() != columnIndex) {
				if (columnIndex + 1 < previewTable.getColumnCount()) {
					if (selectedColumnButtons.get(columnIndex).getSelection()) {

						// in uncertainty mode each second column is flagged
						// with
						// "CERTAINTY"
						// if (buttonUncertaintyDataProvided.getSelection()
						// && (columnIndex % 2 != 0)) {
						// inputPattern.add(new ColumnDescription(columnIndex -
						// 1,
						// "CERTAINTY",
						// ColumnDescription.CONTINUOUS));
						// continue;
						// }

						// here we try to guess the datatype
						// TODO: move this to the preview window where it can be
						// modified by the user

						// fixme this does not work for categorical data
						inputPattern.add(createColumnDescription(columnIndex));

						String labelText = dataMatrix.get(0).get(columnIndex);
						dimensionLabels.add(labelText);
					}
				} else {
					inputPattern.add(createColumnDescription(columnIndex));

					String labelText = dataMatrix.get(0).get(columnIndex);
					dimensionLabels.add(labelText);
				}
			}
		}

		dataSetDescription.setParsingPattern(inputPattern);
		dataSetDescription.setDataSourcePath(fileNameTextField.getText());
		// dataSetDescripton.setColumnLabels(dimidMappingManagerensionLabels);

	}

	/**
	 * Creates a {@link ColumnDescription} for the specified column.
	 * 
	 * @param columnIndex
	 *            Index of the column in the file.
	 * @return The ColumnDescription.
	 */
	private ColumnDescription createColumnDescription(int columnIndex) {
		String dataType = "FLOAT";
		try {
			int testSize = previewTable.getItemCount() - 1;
			for (int rowIndex = dataSetDescription.getNumberOfHeaderLines(); rowIndex < testSize; rowIndex++) {
				if (rowIndex != dataSetDescription.getRowOfColumnIDs()) {
					String testString = dataMatrix.get(rowIndex).get(columnIndex);
					if (!testString.isEmpty())
						Float.parseFloat(testString);
				}
			}
		} catch (NumberFormatException nfe) {
			dataType = "STRING";
		}

		return new ColumnDescription(columnIndex, dataType, ColumnDescription.CONTINUOUS);
	}

	public DataSetDescription getLoadDataParameters() {
		return dataSetDescription;
	}

	public void setLoadDataParameters(DataSetDescription dataSetDescripton) {
		this.dataSetDescription = dataSetDescripton;
	}

	protected void setMostProbableRecordIDType(IDType mostProbableRecordIDType) {

		if (mostProbableRecordIDType == null) {
			rowIDTypes.clear();
			if (rowIDCategory != null)
				rowIDTypes = new ArrayList<IDType>(rowIDCategory.getIdTypes());
			rowIDCombo.clearSelection();
			rowIDCombo.setText("<Please select>");
		} else {
			for (int itemIndex = 0; itemIndex < registeredIDCategories.size(); itemIndex++) {
				if (registeredIDCategories.get(itemIndex) == mostProbableRecordIDType
						.getIDCategory()) {
					rowIDCategoryCombo.select(itemIndex);
					rowIDCategory = mostProbableRecordIDType.getIDCategory();

					// If a genetic ID type is detected for the rows,
					// then SAMPLE is chosen for the columns
					if (rowIDCategory == IDCategory.getIDCategory("GENE")) {
						columnIDCategory = IDCategory.getIDCategory("SAMPLE");
						columnIDCategoryCombo.select(registeredIDCategories
								.indexOf(columnIDCategory));

						updateIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);

						columnIDCombo.select(columnIDTypes.indexOf(IDType
								.getIDType("SAMPLE")));
					}
					break;
				}
			}

			updateIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
			rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));

			TableColumn idColumn = previewTable.getColumn(1);
			idColumn.setText(mostProbableRecordIDType.getTypeName());
		}
	}

	protected ArrayList<IDCategory> getAvailableIDCategories() {
		return registeredIDCategories;
	}

	protected void updateWidgetsAccordingToTableChanges() {
		columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		rowOfColumnIDSpinner.setMaximum(totalNumberOfRows);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(true);
		tableInfoLabel.setText((previewTable.getColumnCount() - 1) + " of "
				+ totalNumberOfColumns + " columns shown");
	}

	@Override
	public boolean isPageComplete() {
		if (fileNameTextField.getText().isEmpty()) {
			// MessageDialog.openError(new Shell(), "Invalid filename",
			// "Please specify a file to load");
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}

		if (rowIDCombo.getSelectionIndex() == -1) {
			// MessageDialog.openError(new Shell(), "Invalid row ID type",
			// "Please select the ID type of the rows");
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}

		if (columnIDCombo.getSelectionIndex() == -1) {
			// MessageDialog.openError(new Shell(), "Invalid column ID type",
			// "Please select the ID type of the columns");
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}
		((DataImportWizard) getWizard()).setRequiredDataSpecified(true);

		return super.isPageComplete();
	}

	// @Override
	// public boolean canFlipToNextPage() {
	//
	//
	//
	// return super.canFlipToNextPage();
	// }

	@Override
	public IWizardPage getNextPage() {

		return super.getNextPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

}
