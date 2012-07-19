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
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.MatrixDefinition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.List;
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
public class LoadDataSetPage extends WizardPage {

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

	private Text dataSetLabelTextField;
	private Text minTextField;
	private Text maxTextField;

	private List columnGroupingsList;
	private List rowGroupingsList;

	private Button buttonHomogeneous;
	private Button buttonSwapRowsWithColumns;

	private Combo columnIDCategoryCombo;
	private Combo rowIDCategoryCombo;

	private String filePath = "";

	private DataSetDescription dataSetDescription;

	private String mathFilterMode = "Log2";

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
	 * {@link GroupingParseSpecification}s for column groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
	/**
	 * {@link GroupingParseSpecification}s for row groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();

	/**
	 * Manager for {@link #previewTable} that extends its features.
	 */
	private PreviewTableManager previewTableManager;

	/**
	 * @param pageName
	 */
	protected LoadDataSetPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {

		dataSetDescription = new DataSetDescription();
		dataSetDescription.setDelimiter("\t");
		dataSetDescription.setNumberOfHeaderLines(1);
		dataSetDescription.setRowOfColumnIDs(0);
		dataSetDescription.setColumnOfRowIds(0);
		registeredIDCategories = new ArrayList<IDCategory>();
		registeredIDCategories.addAll(IDCategory.getAllRegisteredIDCategories());

		int numGridCols = 4;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		Group inputFileGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(SWT.BEGINNING);
		gridData.horizontalSpan = 2;
		inputFileGroup.setLayoutData(gridData);

		Button buttonFileChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonFileChooser.setText("Choose data file...");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		fileNameTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileNameTextField.setEnabled(false);

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

		Composite groupingComposite = new Composite(parentComposite, SWT.NONE);
		groupingComposite.setLayout(new GridLayout(2, true));
		groupingComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

		createGroupingGroup(groupingComposite, "Column Groupings",
				columnGroupingSpecifications, true);
		createGroupingGroup(groupingComposite, "Row Groupings",
				rowGroupingSpecifications, false);

		Composite idComposite = new Composite(parentComposite, SWT.NONE);
		idComposite.setLayout(new GridLayout(4, false));
		idComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		createIDCategoryGroup(idComposite, "Row ID category", false);
		createIDTypeGroup(idComposite, false);

		createIDCategoryGroup(idComposite, "Column ID category", true);
		createIDTypeGroup(idComposite, true);

		Button createIDCategoryButton = new Button(parentComposite, SWT.PUSH);
		createIDCategoryButton.setText("Create ID category");
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

		Button createIDTypeButton = new Button(parentComposite, SWT.PUSH);
		createIDTypeButton.setText("Create ID type");
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

		Group dataSetLabelGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		dataSetLabelGroup.setText("Data set name");
		dataSetLabelGroup.setLayout(new GridLayout(1, false));
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		dataSetLabelGroup.setLayoutData(gridData);

		dataSetLabelTextField = new Text(dataSetLabelGroup, SWT.BORDER);
		dataSetLabelTextField.setText(determineDataSetLabel());
		dataSetLabelTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group startParseAtLineGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		startParseAtLineGroup.setText("Number of header rows");
		startParseAtLineGroup.setLayout(new GridLayout(1, false));

		numHeaderRowsSpinner = new Spinner(startParseAtLineGroup, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(1);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
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

		Group rowOfColumnIDGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		rowOfColumnIDGroup.setText("Row with column IDs");
		rowOfColumnIDGroup.setLayout(new GridLayout(1, false));

		rowOfColumnIDSpinner = new Spinner(rowOfColumnIDGroup, SWT.BORDER);
		rowOfColumnIDSpinner.setMinimum(1);
		rowOfColumnIDSpinner.setMaximum(Integer.MAX_VALUE);
		rowOfColumnIDSpinner.setIncrement(1);
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

		Group columnOfRowIDGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		columnOfRowIDGroup.setText("Column with row IDs");
		columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		columnOfRowIDSpinner = new Spinner(columnOfRowIDGroup, SWT.BORDER);
		columnOfRowIDSpinner.setMinimum(1);
		columnOfRowIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnOfRowIDSpinner.setIncrement(1);
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

		createDelimiterGroup(parentComposite);
		createFilterGroup();
		createDataPropertiesGroup();

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		// previewTable.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
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
			mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTableFromFile();
		}
		setControl(parentComposite);
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

		updateIDTypeCombo(isColumnIDTypeGroup ? columnIDCategory : rowIDCategory,
				idTypes, idCombo);
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

			idTypes = new ArrayList<IDType>(allIDTypesOfCategory.size());
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

	private void createGroupingGroup(Composite parent, String groupLabel,
			final ArrayList<GroupingParseSpecification> groupingParseSpecifications,
			final boolean isColumnGrouping) {

		Group groupingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupingsGroup.setText(groupLabel);
		groupingsGroup.setLayout(new GridLayout(2, false));
		groupingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button addGroupingButton = new Button(groupingsGroup, SWT.PUSH);
		if (isColumnGrouping) {
			columnGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			columnGroupingsList
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} else {
			rowGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			rowGroupingsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		addGroupingButton.setText("Add");
		addGroupingButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

		addGroupingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				ImportGroupingDialog importGroupingDialog = new ImportGroupingDialog(
						new Shell());

				importGroupingDialog.setRowIDCategory(isColumnGrouping ? columnIDCategory
						: rowIDCategory);

				int status = importGroupingDialog.open();

				GroupingParseSpecification groupingParseSpecification = importGroupingDialog
						.getGroupingParseSpecification();

				if (status == Dialog.OK && groupingParseSpecification != null) {
					groupingParseSpecifications.add(groupingParseSpecification);

					String groupingDataSetName = groupingParseSpecification
							.getDataSourcePath().substring(
									groupingParseSpecification.getDataSourcePath()
											.lastIndexOf(File.separator) + 1,
									groupingParseSpecification.getDataSourcePath()
											.lastIndexOf("."));
					if (isColumnGrouping) {
						columnGroupingsList.add(groupingDataSetName);
					} else {
						rowGroupingsList.add(groupingDataSetName);
					}
				}
			}
		});
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
				determineRowIDType();
				previewTableManager.updateTableColors(
						dataSetDescription.getNumberOfHeaderLines(),
						dataSetDescription.getRowOfColumnIDs() + 1,
						dataSetDescription.getColumnOfRowIds() + 1);
				updateWidgetsAccordingToTableChanges();
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
		Group recordIDCategoryGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		recordIDCategoryGroup.setText(groupLabel);
		recordIDCategoryGroup.setLayout(new RowLayout());
		recordIDCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
		Combo idCategoryCombo = new Combo(recordIDCategoryGroup, SWT.DROP_DOWN);
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

	private void createFilterGroup() {
		Group filterGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		filterGroup.setText("Apply filter");
		filterGroup.setLayout(new RowLayout());
		filterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo mathFilterCombo = new Combo(filterGroup, SWT.DROP_DOWN);
		String[] filterOptions = { "Normal", "Log10", "Log2" };
		mathFilterCombo.setItems(filterOptions);
		mathFilterCombo.setEnabled(true);
		mathFilterCombo.select(2);
		mathFilterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mathFilterMode = mathFilterCombo.getText();
			}
		});

		final Button buttonMin = new Button(filterGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minTextField.setEnabled(buttonMin.getSelection());
			}
		});

		minTextField = new Text(filterGroup, SWT.BORDER);
		minTextField.setEnabled(false);
		minTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
			}
		});

		final Button buttonMax = new Button(filterGroup, SWT.CHECK);
		buttonMax.setText("Max");
		buttonMax.setEnabled(true);
		buttonMax.setSelection(false);
		buttonMax.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxTextField.setEnabled(buttonMax.getSelection());
			}
		});

		maxTextField = new Text(filterGroup, SWT.BORDER);
		maxTextField.setEnabled(false);
		maxTextField.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
			}
		});
	}

	private void createDataPropertiesGroup() {
		Group dataPropertiesGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		dataPropertiesGroup.setText("Data properties");
		dataPropertiesGroup.setLayout(new RowLayout());
		dataPropertiesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonHomogeneous = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonHomogeneous.setText("Homogeneous data");
		buttonHomogeneous.setEnabled(true);
		buttonHomogeneous.setSelection(true);

		buttonSwapRowsWithColumns = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonSwapRowsWithColumns.setText("Swap rows and columns");
		buttonSwapRowsWithColumns.setEnabled(true);
		buttonSwapRowsWithColumns.setSelection(false);
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	private void fillDatasetDescription() {
		if (minTextField.getEnabled() && !minTextField.getText().isEmpty()) {
			float fMin = Float.parseFloat(minTextField.getText());
			if (!Float.isNaN(fMin)) {
				dataSetDescription.setMin(fMin);
			}
		}
		if (maxTextField.getEnabled() && !maxTextField.getText().isEmpty()) {
			float fMax = Float.parseFloat(maxTextField.getText());
			if (!Float.isNaN(fMax)) {
				dataSetDescription.setMax(fMax);
			}
		}
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

		dataSetDescription.setRowIDSpecification(rowIDSpecification);
		dataSetDescription.setMathFilterMode(mathFilterMode);
		dataSetDescription.setDataHomogeneous(buttonHomogeneous.getSelection());
		dataSetDescription.setTransposeMatrix(buttonSwapRowsWithColumns.getSelection());
		dataSetDescription.setDataSetName(dataSetLabelTextField.getText());
		dataSetDescription.setColumnGroupingSpecifications(columnGroupingSpecifications);
		dataSetDescription.setRowGroupingSpecifications(rowGroupingSpecifications);

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

}
