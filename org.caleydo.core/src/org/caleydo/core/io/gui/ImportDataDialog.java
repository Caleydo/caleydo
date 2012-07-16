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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.UUID;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.util.LabelEditorDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.RCPViewInitializationData;
import org.caleydo.core.view.RCPViewManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 */
public class ImportDataDialog extends Dialog {

	private static int MAX_PREVIEW_TABLE_ROWS = 50;
	private static int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	private Composite composite;

	private Text txtDataSetLabel;
	private Text txtFileName;
	private Text txtStartParseAtLine;
	private Text txtMin;
	private Text txtMax;

	private List listColumnGroupings;
	private List listRowGroupings;

	private Button buttonHomogeneous;
	private Button buttonUncertaintyDataProvided;
	private Button buttonSwapRowsWithColumns;
	/**
	 * Button for adding {@link GroupingParseSpecification} for columns.
	 */
	private Button addColumnGroupingSpecificationButton;
	/**
	 * Button for adding {@link GroupingParseSpecification} for rows.
	 */
	private Button addRowGroupingSpecificationButton;

	private Table previewTable;

	private ArrayList<Button> skipColumn = new ArrayList<Button>();

	private Combo rowIDCombo;
	private Combo columnIDCombo;

	private Combo columnIDCategoryCombo;
	private Combo rowIDCategoryCombo;

	private ArrayList<IDType> rowIDTypes;
	private ArrayList<IDType> columnIDTypes;

	private String inputFile = "";
	private String filePath = "";

	private DataSetDescription dataSetDescription = new DataSetDescription();

	private String mathFilterMode = "Log2";

	private ArrayList<IDCategory> allRegisteredIDCategories = new ArrayList<IDCategory>();

	/**
	 * {@link GroupingParseSpecification}s for column groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
	/**
	 * {@link GroupingParseSpecification}s for row groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();

	private IDCategory rowIDCategory;

	private IDCategory columnIDCategory;

	public ImportDataDialog(Shell parentShell) {
		super(parentShell);
	}

	public ImportDataDialog(Shell parentShell, String inputFile) {
		this(parentShell);
		this.inputFile = inputFile;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open Text Data File");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		return parent;
	}

	@Override
	protected void okPressed() {

		if (txtFileName.getText().isEmpty()) {
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

		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		composite.setLayout(layout);

		Group inputFileGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(SWT.BEGINNING);
		gridData.horizontalSpan = 2;
		inputFileGroup.setLayoutData(gridData);

		Button buttonFileChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonFileChooser.setText("Choose data file...");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		txtFileName = new Text(inputFileGroup, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				inputFile = fileDialog.open();

				if (inputFile == null)
					return;

				dataSetDescription.setDataSourcePath(inputFile);
				txtFileName.setText(inputFile);

				txtDataSetLabel.setText(determineDataSetLabel());

				createDataPreviewTable("\t");
			}
		});

		Composite groupingComposite = new Composite(composite, SWT.NONE);
		groupingComposite.setLayout(new GridLayout(2, true));
		groupingComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

		createGroupingGroup(groupingComposite, "Column Groupings",
				columnGroupingSpecifications, true);
		createGroupingGroup(groupingComposite, "Row Groupings",
				rowGroupingSpecifications, false);

		allRegisteredIDCategories.clear();
		allRegisteredIDCategories.addAll(IDCategory.getAllRegisteredIDCategories());

		Composite idComposite = new Composite(composite, SWT.NONE);
		idComposite.setLayout(new GridLayout(4, false));
		idComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		createIDCategoryGroup(idComposite, "Row ID category", false);
		createIDTypeGroup(idComposite, false);
		createIDCategoryGroup(idComposite, "Column ID category", true);
		createIDTypeGroup(idComposite, true);

		Group dataSetLabelGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		dataSetLabelGroup.setText("Data set name");
		dataSetLabelGroup.setLayout(new GridLayout(1, false));
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		dataSetLabelGroup.setLayoutData(gridData);

		txtDataSetLabel = new Text(dataSetLabelGroup, SWT.BORDER);
		txtDataSetLabel.setText(determineDataSetLabel());
		txtDataSetLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group startParseAtLineGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		startParseAtLineGroup.setText("Ignore lines in header");
		startParseAtLineGroup.setLayout(new GridLayout(1, false));

		txtStartParseAtLine = new Text(startParseAtLineGroup, SWT.BORDER);
		txtStartParseAtLine.setLayoutData(new GridData(50, 15));
		txtStartParseAtLine.setText("1");
		txtStartParseAtLine.setTextLimit(2);
		txtStartParseAtLine.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// Add 1 because the number that the user enters is human
				// readable and not array index
				// (starting with 0).
				dataSetDescription.setNumberOfHeaderLines(Integer
						.valueOf(txtStartParseAtLine.getText()));

				createDataPreviewTable("\t");
				composite.pack();
			}
		});

		createDelimiterGroup(composite);
		createFilterGroup();
		createDataPropertiesGroup();

		previewTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = numGridCols;
		gridData.heightHint = 400;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		// Check if an external file name is given to the action
		if (!inputFile.isEmpty()) {
			txtFileName.setText(inputFile);
			dataSetDescription.setDataSourcePath(inputFile);
			mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTable("\t");
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
			listColumnGroupings = new List(groupingsGroup, SWT.SINGLE);
			listColumnGroupings
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			addColumnGroupingSpecificationButton = addGroupingButton;
		} else {
			listRowGroupings = new List(groupingsGroup, SWT.SINGLE);
			listRowGroupings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			addRowGroupingSpecificationButton = addGroupingButton;
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
						listColumnGroupings.add(groupingDataSetName);
					} else {
						listRowGroupings.add(groupingDataSetName);
					}
				}
			}
		});
	}

	private String determineDataSetLabel() {

		if (inputFile == null || inputFile.isEmpty())
			return "<insert data set name>";

		return inputFile.substring(inputFile.lastIndexOf(File.separator) + 1,
				inputFile.lastIndexOf("."));
	}

	private void createIDCategoryGroup(Composite parent, String groupLabel,
			final boolean isColumnCategory) {
		Group recordIDCategoryGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		recordIDCategoryGroup.setText(groupLabel);
		recordIDCategoryGroup.setLayout(new RowLayout());
		recordIDCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
		Combo idCategoryCombo = new Combo(recordIDCategoryGroup, SWT.DROP_DOWN);
		idCategoryCombo.setText("Please select...");

		if (isColumnCategory) {
			columnIDCategoryCombo = idCategoryCombo;
		} else {
			rowIDCategoryCombo = idCategoryCombo;
		}

		int index = 0;
		for (IDCategory idCategory : allRegisteredIDCategories) {

			idCategoryCombo.add(idCategory.getCategoryName());

			if (index == 0) {
				if (isColumnCategory) {
					columnIDCategory = idCategory;
				} else {
					rowIDCategory = idCategory;
				}

			}
			index++;
		}

		idCategoryCombo.setEnabled(true);
		idCategoryCombo.select(0);
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

	// private void createIDCategoryGroup(Composite parent) {
	// Group dimensionIDCategoryGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// dimensionIDCategoryGroup.setText("Column ID category");
	// dimensionIDCategoryGroup.setLayout(new RowLayout());
	// dimensionIDCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
	// columnIDCategoryCombo = new Combo(dimensionIDCategoryGroup,
	// SWT.DROP_DOWN);
	//
	// int index = 0;
	// for (IDCategory idCategory : allRegisteredIDCategories) {
	//
	// columnIDCategoryCombo.add(idCategory.getCategoryName());
	//
	// if (index == 0)
	// columnIDCategory = idCategory;
	// index++;
	// }
	//
	// columnIDCategoryCombo.setEnabled(true);
	// columnIDCategoryCombo.select(0);
	// columnIDCategoryCombo.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// columnIDCategory = IDCategory.getIDCategory(columnIDCategoryCombo
	// .getItem(columnIDCategoryCombo.getSelectionIndex()));
	// fillColumnIDTypeCombo();
	// }
	// });
	// }

	private void createIDTypeGroup(Composite parent, boolean isColumnIDTypeGroup) {
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

	// private void createDimensionIDTypeGroup(Composite parent) {
	// Group idTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
	// idTypeGroup.setText("Column ID type");
	// idTypeGroup.setLayout(new RowLayout());
	// idTypeGroup.setLayoutData(new GridData(SWT.LEFT));
	// columnIDCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
	// columnIDTypes = new ArrayList<IDType>();
	//
	// fillIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);
	// }

	private void fillIDTypeCombo(IDCategory idCategory, ArrayList<IDType> idTypes,
			Combo idTypeCombo) {

		ArrayList<IDType> tempIDTypes = idCategory.getIdTypes();

		idTypes.clear();
		for (IDType idType : tempIDTypes) {
			if (!idType.isInternalType())
				idTypes.add(idType);
		}

		String[] idTypesAsString = new String[idTypes.size()];
		int index = 0;
		for (IDType idType : idTypes) {
			idTypesAsString[index] = idType.getTypeName();
			index++;
		}

		idTypeCombo.setItems(idTypesAsString);
		idTypeCombo.setEnabled(true);
		idTypeCombo.select(0);

	}

	// private void fillColumnIDTypeCombo() {
	// ArrayList<IDType> tempIDTypes = columnIDCategory.getIdTypes();
	//
	// dimensionIDTypes.clear();
	// for (IDType idType : tempIDTypes) {
	// if (!idType.isInternalType())
	// dimensionIDTypes.add(idType);
	// }
	//
	// String[] idTypesAsString = new String[dimensionIDTypes.size()];
	// int index = 0;
	// for (IDType idType : dimensionIDTypes) {
	// idTypesAsString[index] = idType.getTypeName();
	// index++;
	// }
	//
	// dimensionIDCombo.setItems(idTypesAsString);
	// dimensionIDCombo.setEnabled(true);
	// dimensionIDCombo.select(0);
	// }

	private void createDelimiterGroup(Composite parent) {
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
				createDataPreviewTable(customizedDelimiterTextField.getText());
				// composite.pack();
			}

		});

		SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				if (selectedButton != delimiterButtons[5]) {
					createDataPreviewTable((String) selectedButton.getData());
					customizedDelimiterTextField.setEnabled(false);
				} else {
					customizedDelimiterTextField.setEnabled(true);
					createDataPreviewTable(" ");
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}

	private void createFilterGroup() {
		Group filterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
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
				txtMin.setEnabled(buttonMin.getSelection());
			}
		});

		txtMin = new Text(filterGroup, SWT.BORDER);
		txtMin.setEnabled(false);
		txtMin.addListener(SWT.Verify, new Listener() {
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
				txtMax.setEnabled(buttonMax.getSelection());
			}
		});

		txtMax = new Text(filterGroup, SWT.BORDER);
		txtMax.setEnabled(false);
		txtMax.addListener(SWT.Verify, new Listener() {
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
		Group dataPropertiesGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		dataPropertiesGroup.setText("Data properties");
		dataPropertiesGroup.setLayout(new RowLayout());
		dataPropertiesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonHomogeneous = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonHomogeneous.setText("Homogeneous data");
		buttonHomogeneous.setEnabled(true);
		buttonHomogeneous.setSelection(true);

		buttonUncertaintyDataProvided = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonUncertaintyDataProvided.setText("Uncertainty data");
		buttonUncertaintyDataProvided.setEnabled(true);

		buttonSwapRowsWithColumns = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonSwapRowsWithColumns.setText("Swap rows and columns");
		buttonSwapRowsWithColumns.setEnabled(true);
		buttonSwapRowsWithColumns.setSelection(false);
	}

	private void createDataPreviewTable(final String sDelimiter) {

		this.dataSetDescription.setDelimiter(sDelimiter);

		// boolean clusterInfo = false;

		// Clear table if not empty
		previewTable.removeAll();

		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		final TableEditor editor = new TableEditor(previewTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Read preview table
		BufferedReader file;
		try {
			file = GeneralManager.get().getResourceLoader()
					.getResource(dataSetDescription.getDataSourcePath());

			String line = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < dataSetDescription
					.getNumberOfHeaderLines() - 1; iIgnoreLineIndex++) {
				file.readLine();
			}

			String nextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			TableItem item;
			int colIndex = 0;

			// Read labels
			if ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, sDelimiter, false);
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);
				column.setText("");

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					final TableColumn dataColumn = new TableColumn(previewTable, SWT.NONE);
					dataColumn.setWidth(100);
					dataColumn.setText(nextToken);

					dataColumn.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							LabelEditorDialog dialog = new LabelEditorDialog(new Shell());
							String sLabel = dialog.open(dataColumn.getText());

							if (sLabel != null && !sLabel.isEmpty()) {
								dataColumn.setText(sLabel);
							}
						}
					});

					colIndex++;
				}
			}

			createDataClassBar();

			int rowCount = 0;
			boolean isCellFilled = false;

			// Read raw data
			while ((line = file.readLine()) != null && rowCount < MAX_PREVIEW_TABLE_ROWS) {
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(line, sDelimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
				item.setText("Row " + (rowCount + 1)); // +1 to be intuitive for
														// a non programmer :)
				colIndex = 0;

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					// Check for empty cells
					if (nextToken.equals(sDelimiter) && !isCellFilled) {
						item.setText(colIndex + 1, "");
						colIndex++;
					} else if (nextToken.equals(sDelimiter) && isCellFilled) {
						isCellFilled = false; // reset
					} else {
						isCellFilled = true;
						item.setText(colIndex + 1, nextToken);
						colIndex++;
					}
				}

				isCellFilled = false; // reset

				rowCount++;
			}

			// check for experiment cluster info in the rest of the file
			while ((line = file.readLine()) != null) {

				tokenizer = new StringTokenizer(line, sDelimiter, true);

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
	}

	private void createDataClassBar() {

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE);
		tmpItem.setText("Use column");

		Button skipButton;
		for (int iColIndex = 2; iColIndex < previewTable.getColumnCount(); iColIndex++) {
			skipButton = new Button(previewTable, SWT.CHECK | SWT.CENTER);
			skipButton.setSelection(true);
			skipButton.setData("column", iColIndex);
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

			skipColumn.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, iColIndex);
		}
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	private void fillDatasetDescription() {
		if (txtMin.getEnabled() && !txtMin.getText().isEmpty()) {
			float fMin = Float.parseFloat(txtMin.getText());
			if (!Float.isNaN(fMin)) {
				dataSetDescription.setMin(fMin);
			}
		}
		if (txtMax.getEnabled() && !txtMax.getText().isEmpty()) {
			float fMax = Float.parseFloat(txtMax.getText());
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
		dataSetDescription.setDataSetName(txtDataSetLabel.getText());
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
		for (int columnIndex = 2; columnIndex < previewTable.getColumnCount(); columnIndex++) {

			if (!skipColumn.get(columnIndex - 2).getSelection()) {
				// do nothing
			} else {

				// in uncertainty mode each second column is flagged with
				// "CERTAINTY"
				if (buttonUncertaintyDataProvided.getSelection()
						&& (columnIndex % 2 != 0)) {
					inputPattern.add(new ColumnDescription(columnIndex - 1, "CERTAINTY",
							ColumnDescription.CONTINUOUS));
					continue;
				}

				// here we try to guess the datatype
				// TODO: move this to the preview window where it can be
				// modified by the user
				String dataType = "FLOAT";
				try {
					int testSize = previewTable.getItemCount()
							- dataSetDescription.getNumberOfHeaderLines() - 1;
					for (int rowCount = 1; rowCount < testSize; rowCount++) {
						String testString = previewTable.getItem(rowCount).getText(
								columnIndex);
						if (!testString.isEmpty())
							Float.parseFloat(testString);
					}
				} catch (NumberFormatException nfe) {
					dataType = "STRING";
				}
				// fixme this does not work for categorical data
				inputPattern.add(new ColumnDescription(columnIndex - 1, dataType,
						ColumnDescription.CONTINUOUS));

				String labelText = previewTable.getColumn(columnIndex).getText();
				dimensionLabels.add(labelText);
			}
		}

		dataSetDescription.setParsingPattern(inputPattern);
		dataSetDescription.setDataSourcePath(txtFileName.getText());
		// dataSetDescripton.setColumnLabels(dimidMappingManagerensionLabels);

	}

	public DataSetDescription getLoadDataParameters() {
		return dataSetDescription;
	}

	public void setLoadDataParameters(DataSetDescription dataSetDescripton) {
		this.dataSetDescription = dataSetDescripton;
	}

	private void determineRowIDType() {

		TableItem[] items = previewTable.getItems();
		ArrayList<String> idList = new ArrayList<String>();
		int rowIndex = 1;
		while (rowIndex < items.length
				&& rowIndex <= MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION) {
			idList.add(items[rowIndex].getText(1));
			rowIndex++;
		}

		int maxCorrectElements = 0;
		IDType mostProbableIDType = null;

		for (IDCategory idCategory : allRegisteredIDCategories) {

			rowIDTypes = new ArrayList<IDType>();
			HashSet<IDType> alIDTypesTemp = IDMappingManagerRegistry.get()
					.getIDMappingManager(idCategory).getIDTypes();
			for (IDType idType : alIDTypesTemp) {
				if (!idType.isInternalType())
					rowIDTypes.add(idType);
			}

			IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(idCategory);

			for (IDType idType : rowIDTypes) {

				int currentCorrectElements = 0;

				for (String currentID : idList) {

					if (idType.getColumnType().equals(EColumnType.INT)) {
						try {
							Integer idInt = Integer.valueOf(currentID);
							if (idMappingManager.doesElementExist(idType, idInt)) {
								currentCorrectElements++;
							}
						} catch (NumberFormatException e) {
						}
					} else if (idType.getColumnType().equals(EColumnType.STRING)) {
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

	private void setMostProbableRecordIDType(IDType mostProbableRecordIDType) {

		for (int itemIndex = 0; itemIndex < allRegisteredIDCategories.size(); itemIndex++) {
			if (allRegisteredIDCategories.get(itemIndex) == mostProbableRecordIDType
					.getIDCategory()) {
				rowIDCategoryCombo.select(itemIndex);
				rowIDCategory = mostProbableRecordIDType.getIDCategory();

				// If a genetic ID type is detected for the rows,
				// then SAMPLE is chosen for the columns
				if (rowIDCategory == IDCategory.getIDCategory("GENE")) {
					columnIDCategory = IDCategory.getIDCategory("SAMPLE");
					columnIDCategoryCombo.select(allRegisteredIDCategories
							.indexOf(columnIDCategory));

					fillIDTypeCombo(columnIDCategory, columnIDTypes, columnIDCombo);

					columnIDCombo
							.select(columnIDTypes.indexOf(IDType.getIDType("SAMPLE")));
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
