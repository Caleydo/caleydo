/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.view.RCPViewInitializationData;
import org.caleydo.core.view.RCPViewManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 */
public class ImportDataDialog extends AImportDialog {

	private Text dataSetLabelTextField;
	private Text minTextField;
	private Text maxTextField;

	private List columnGroupingsList;
	private List rowGroupingsList;

	private Button buttonHomogeneous;
	// private Button buttonUncertaintyDataProvided;
	private Button buttonSwapRowsWithColumns;

	private Combo columnIDCategoryCombo;
	private Combo rowIDCategoryCombo;

	private String filePath = "";

	private DataSetDescription dataSetDescription;

	private String mathFilterMode = "Log2";

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

	private IDCategory columnIDCategory;

	public ImportDataDialog(Shell parentShell) {
		super(parentShell);
	}

	public ImportDataDialog(Shell parentShell, String inputFile) {
		this(parentShell);
		this.inputFileName = inputFile;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open Data File");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		dataSetDescription.setDelimiter("\t");
		dataSetDescription.setNumberOfHeaderLines(0);
		dataSetDescription.setRowOfColumnIDs(0);
		dataSetDescription.setColumnOfRowIds(0);
		registeredIDCategories = new ArrayList<IDCategory>();
		registeredIDCategories.addAll(IDCategory.getAllRegisteredIDCategories());
		createGUI(parent);
		return parent;
	}

	@Override
	protected void okPressed() {

		if (fileNameTextField.getText().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid filename",
					"Please specify a file to load");
			return;
		}

		if (rowIDCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid row ID type",
					"Please select the ID type of the rows");
			return;
		}

		if (columnIDCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid column ID type",
					"Please select the ID type of the columns");
			return;
		}

		fillDatasetDescription();

		ATableBasedDataDomain dataDomain;
		try {
			dataDomain = DataLoader.loadData(dataSetDescription);
		} catch (FileNotFoundException e1) {
			// TODO do something intelligent
			e1.printStackTrace();
			throw new IllegalStateException();

		} catch (IOException e1) {
			// TODO do something intelligent
			e1.printStackTrace();
			throw new IllegalStateException();
		}

		// Open default start view for the newly created data domain
		try {

			String secondaryID = UUID.randomUUID().toString();
			RCPViewInitializationData rcpViewInitData = new RCPViewInitializationData();
			rcpViewInitData.setDataDomainID(dataDomain.getDataDomainID());
			RCPViewManager.get().addRCPView(secondaryID, rcpViewInitData);

			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.showView(dataDomain.getDefaultStartViewType(), secondaryID,
								IWorkbenchPage.VIEW_ACTIVATE);

			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.okPressed();

	}

	@Override
	protected void cancelPressed() {

		super.cancelPressed();
	}

	private void createGUI(Composite parent) {

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
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
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
		numHeaderRowsSpinner.setMinimum(0);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateTableColors();
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
				updateTableColors();
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
				updateTableColors();
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

		// Check if an external file name is given to the action
		if (!inputFileName.isEmpty()) {
			fileNameTextField.setText(inputFileName);
			dataSetDescription.setDataSourcePath(inputFileName);
			mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTableFromFile();
		}
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
		for (IDCategory idCategory : registeredIDCategories) {

			idCategoryCombo.add(idCategory.getCategoryName());

			// if (index == 0) {
			// if (isColumnCategory) {
			// columnIDCategory = idCategory;
			// } else {
			// rowIDCategory = idCategory;
			// }
			//
			// }
			// index++;
		}

		// idCategoryCombo.setEnabled(false);
		// idCategoryCombo.deselect(0);
		idCategoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isColumnCategory) {
					columnIDCategory = IDCategory.getIDCategory(columnIDCategoryCombo
							.getItem(columnIDCategoryCombo.getSelectionIndex()));
					fillIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);
				} else {
					rowIDCategory = IDCategory.getIDCategory(rowIDCategoryCombo
							.getItem(rowIDCategoryCombo.getSelectionIndex()));
					fillIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
				}

			}
		});
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
				// for (char c : chars) {
				// TODO
				// if (!('0' <= chars[i] && chars[i] <= '9'))
				// {
				// e.doit = false;
				// return;
				// }
				// }
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
				// for (char c : chars) {
				// TODO
				// if (!('0' <= chars[i] && chars[i] <= '9'))
				// {
				// e.doit = false;
				// return;
				// }
				// }
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

		// buttonUncertaintyDataProvided = new Button(dataPropertiesGroup,
		// SWT.CHECK);
		// buttonUncertaintyDataProvided.setText("Uncertainty data");
		// buttonUncertaintyDataProvided.setEnabled(true);

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
			rowIDSpecification.setSubStringExpression("\\.");
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

						String labelText = dataMatrix[0][columnIndex];
						dimensionLabels.add(labelText);
					}
				} else {
					inputPattern.add(createColumnDescription(columnIndex));

					String labelText = dataMatrix[0][columnIndex];
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
					String testString = dataMatrix[rowIndex][columnIndex];
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

						fillIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);

						columnIDCombo.select(columnIDTypes.indexOf(IDType
								.getIDType("SAMPLE")));
					}

					break;
				}
			}

			fillIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
			rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));

			TableColumn idColumn = previewTable.getColumn(1);
			idColumn.setText(mostProbableRecordIDType.getTypeName());
		}
	}

	@Override
	protected MatrixDefinition createConcreteMatrixDefinition() {
		dataSetDescription = new DataSetDescription();
		return dataSetDescription;
	}

	@Override
	protected ArrayList<IDCategory> getAvailableIDCategories() {
		return registeredIDCategories;
	}

	@Override
	protected boolean allowsColumnIDs() {
		return true;
	}

	@Override
	protected void previewTableCreatedFromFile() {
		columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		rowOfColumnIDSpinner.setMaximum(totalNumberOfRows);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(true);
	}

}
