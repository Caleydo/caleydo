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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 * 
 * @author Christian Partl
 * 
 */
public class ImportGroupingDialog extends Dialog implements ITabularDataImporter {

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
	 * The row id category for which groupings should be loaded.
	 */
	protected IDCategory rowIDCategory;

	/**
	 * Combo box to specify the row ID Type.
	 */
	protected Combo rowIDCombo;

	/**
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	protected ArrayList<IDType> rowIDTypes;

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
	 * The {@link GroupingParseSpecification} created using this dialog.
	 */
	private GroupingParseSpecification groupingParseSpecification;

	/**
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	/**
	 * Manager for {@link #previewTable} that extends its features.
	 */
	private PreviewTableManager previewTableManager;

	/**
	 * Textfield for the grouping name.
	 */
	private Text groupingNameTextField;

	/**
	 * @param parentShell
	 */
	public ImportGroupingDialog(Shell parentShell) {
		super(parentShell);
		parentShell.setText("Import Grouping");
		groupingParseSpecification = new GroupingParseSpecification();
		groupingParseSpecification.setDelimiter("\t");
		groupingParseSpecification.setNumberOfHeaderLines(1);
	}

	/**
	 * @param parentShell
	 * @param groupingParseSpecification
	 *            {@link GroupingParseSpecification} that will be used to
	 *            initialize the widgets of this dialog.
	 */
	public ImportGroupingDialog(Shell parentShell,
			GroupingParseSpecification groupingParseSpecification) {
		super(parentShell);
		parentShell.setText("Import Grouping");
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
		if (groupingParseSpecification.getDataSourcePath() != null)
			inputFileName = groupingParseSpecification.getDataSourcePath();
	}

	@Override
	protected void okPressed() {

		if (fileNameTextField.getText().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename",
					"Please specify a file to load");
			return;
		}

		if (rowIDCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid Row ID Type",
					"Please select the ID type of the rows");
			return;
		}

		ArrayList<Integer> selectedColumns = new ArrayList<Integer>();
		for (int columnIndex = 0; columnIndex < totalNumberOfColumns; columnIndex++) {

			if (groupingParseSpecification.getColumnOfRowIds() != columnIndex) {
				if (columnIndex + 1 < previewTable.getColumnCount()) {
					if (selectedColumnButtons.get(columnIndex).getSelection()) {
						selectedColumns.add(columnIndex);
					}
				} else {
					selectedColumns.add(columnIndex);
				}
			}
		}
		groupingParseSpecification.setColumns(selectedColumns);
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
		groupingParseSpecification.setRowIDSpecification(rowIDSpecification);
		groupingParseSpecification.setContainsColumnIDs(false);
		groupingParseSpecification.setGroupingName(groupingNameTextField.getText());

		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		return parent;
	}

	private void createGUI(Composite parent) {

		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		Group inputFileGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input File");
		inputFileGroup.setLayout(new GridLayout(2, false));
		inputFileGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button openFileButton = new Button(inputFileGroup, SWT.PUSH);
		openFileButton.setText("Open Grouping File");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		fileNameTextField.setEnabled(false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 200;
		fileNameTextField.setLayoutData(gridData);

		Group groupingNameGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		groupingNameGroup.setText("Grouping Name");
		groupingNameGroup.setLayout(new GridLayout(1, false));
		groupingNameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		groupingNameTextField = new Text(groupingNameGroup, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 100;
		groupingNameTextField.setLayoutData(gridData);

		openFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
				// fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv;*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				inputFileName = fileDialog.open();

				if (inputFileName == null)
					return;
				fileNameTextField.setText(inputFileName);
				groupingNameTextField.setText(inputFileName.substring(
						inputFileName.lastIndexOf(File.separator) + 1,
						inputFileName.lastIndexOf(".")));

				groupingParseSpecification.setDataSourcePath(inputFileName);
				createDataPreviewTableFromFile();
			}
		});

		createRowConfigPart(parentComposite);

		DelimiterRadioGroup delimiterRadioGroup = new DelimiterRadioGroup();
		delimiterRadioGroup.create(parentComposite, groupingParseSpecification, this);

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		// previewTable.setHeaderVisible(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, numGridCols, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		previewTableManager = new PreviewTableManager(previewTable);

		createTableInfo(parentComposite);

		// Check if an external file name is given to the action
		if (!inputFileName.isEmpty()) {
			fileNameTextField.setText(inputFileName);
			groupingParseSpecification.setDataSourcePath(inputFileName);
			// mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTableFromFile();
		}
	}

	private void createRowConfigPart(Composite parent) {

		Group rowConfigGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		rowConfigGroup.setText("Row Configuration");
		rowConfigGroup.setLayout(new GridLayout(1, false));
		rowConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Composite leftConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label idCategoryLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText("Row ID Class");
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		Label categoryIDLabel = new Label(leftConfigGroupPart, SWT.NONE);
		categoryIDLabel.setText(rowIDCategory.getCategoryName());

		createIDTypeGroup(leftConfigGroupPart);

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(1);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		numHeaderRowsSpinner.setLayoutData(gridData);
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {

					int numHeaderRows = numHeaderRowsSpinner.getSelection();
					groupingParseSpecification.setNumberOfHeaderLines(numHeaderRows);
					previewTableManager.updateTableColors(
							groupingParseSpecification.getNumberOfHeaderLines(), -1,
							groupingParseSpecification.getColumnOfRowIds() + 1);

				} catch (NumberFormatException exc) {

				}

			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with Row IDs");
		// columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		columnOfRowIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnOfRowIDSpinner.setMinimum(1);
		columnOfRowIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnOfRowIDSpinner.setIncrement(1);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		columnOfRowIDSpinner.setLayoutData(gridData);
		columnOfRowIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				groupingParseSpecification.setColumnOfRowIds(columnOfRowIDSpinner
						.getSelection() - 1);
				previewTableManager.updateTableColors(
						groupingParseSpecification.getNumberOfHeaderLines(), -1,
						groupingParseSpecification.getColumnOfRowIds() + 1);
			}
		});

	}

	protected void createIDTypeGroup(Composite parent) {
		Label idTypeLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Row ID Type");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		rowIDCombo = new Combo(parent, SWT.DROP_DOWN);
		rowIDCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rowIDTypes = new ArrayList<IDType>();

		updateIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
	}

	public void createDataPreviewTableFromFile() {
		parser.parse(inputFileName, groupingParseSpecification.getDelimiter(), false,
				MAX_PREVIEW_TABLE_ROWS);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		totalNumberOfRows = parser.getTotalNumberOfRows();
		previewTableManager.createDataPreviewTableFromDataMatrix(dataMatrix,
				MAX_PREVIEW_TABLE_COLUMNS);
		selectedColumnButtons = previewTableManager.getSelectedColumnButtons();
		determineRowIDType();
		previewTableManager.updateTableColors(
				groupingParseSpecification.getNumberOfHeaderLines(), -1,
				groupingParseSpecification.getColumnOfRowIds() + 1);
		updateWidgetsAccordingToTableChanges();

		parentComposite.pack();
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

		if (mostProbableRecordIDType == null) {
			rowIDTypes.clear();
			rowIDTypes = new ArrayList<IDType>(rowIDCategory.getIdTypes());
			rowIDCombo.clearSelection();
			rowIDCombo.setText("<Please Select>");
		} else {
			updateIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
			rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));

			TableColumn idColumn = previewTable.getColumn(1);
			idColumn.setText(mostProbableRecordIDType.getTypeName());
		}
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
				idTypeCombo.setText("<Please Select>");
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

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return groupingParseSpecification;
	}

	/**
	 * @param rowIDCategory
	 *            setter, see {@link #rowIDCategory}
	 */
	public void setRowIDCategory(IDCategory rowIDCategory) {
		this.rowIDCategory = rowIDCategory;
	}

	protected ArrayList<IDCategory> getAvailableIDCategories() {
		ArrayList<IDCategory> idCategories = new ArrayList<IDCategory>();
		idCategories.add(rowIDCategory);
		return idCategories;
	}

	protected void updateWidgetsAccordingToTableChanges() {
		columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
		// showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(true);
		tableInfoLabel.setText((previewTable.getColumnCount() - 1) + " of "
				+ totalNumberOfColumns + " Columns shown");
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
		tableInfoComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true,
				2, 1));

		tableInfoLabel = new Label(tableInfoComposite, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		// This needs to be looked at again: without it, the label is not
		// displayed.
		gd.widthHint = 100;
		tableInfoLabel.setLayoutData(gd);

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
						groupingParseSpecification.getNumberOfHeaderLines(), -1,
						groupingParseSpecification.getColumnOfRowIds() + 1);
				updateWidgetsAccordingToTableChanges();
				showAllColumnsButton.setSelection(showAllColumns);
			}

		});

		Label showAllColumnsLabel = new Label(tableInfoComposite, SWT.NONE);
		showAllColumnsLabel.setText("Show all Columns");
	}

}
