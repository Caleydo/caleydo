/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.gui.util.FontUtil;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.SelectAllNoneWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.INoArgumentCallback;
import org.caleydo.core.io.gui.dataimport.widget.table.PreviewTableWidget;
import org.caleydo.core.util.base.BooleanCallback;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.IntegerCallback;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

/**
 * Page for loading the dataset from file.
 *
 * @author Christian Partl
 *
 */
public class LoadDataSetPage extends AImportDataPage<DataImportWizard> implements Listener {

	public static final String PAGE_NAME = "Load Dataset";

	public static final String PAGE_DESCRIPTION = "Specify the dataset you want to load.";

	/**
	 * Combo box to specify the {@link IDCategory} for the columns of the dataset.
	 */
	protected Combo columnIDCategoryCombo;
	/**
	 * Combo box to specify the {@link IDCategory} for the rows of the dataset.
	 */
	protected Combo rowIDCategoryCombo;

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Combo box to specify the row ID Type.
	 */
	protected Combo rowIDCombo;

	/**
	 * Combo box to specify the column ID Type.
	 */
	protected Combo columnIDCombo;

	/**
	 * Manager for {@link #previewTable} that extends its features.
	 */
	protected PreviewTableWidget previewTable;

	/**
	 * Spinner used to define the index of the row that contains the column ids.
	 */
	protected Spinner rowOfColumnIDSpinner;

	/**
	 * Spinner used to define the index of the column that contains the row ids.
	 */
	protected Spinner columnOfRowIDSpinner;

	/**
	 * Spinner used to define the index of the row from where on data is contained.
	 */
	protected Spinner numHeaderRowsSpinner;

	/**
	 * Spinner used to define the index of the column from where on data is contained.
	 */
	protected Spinner dataStartColumnSpinner;

	/**
	 * Button to create a new {@link IDCategory}.
	 */
	protected Button createRowIDCategoryButton;

	/**
	 * Button to create a new {@link IDCategory}.
	 */
	protected Button createColumnIDCategoryButton;

	/**
	 * Button to create a new {@link IDType}.
	 */
	protected Button createRowIDTypeButton;

	/**
	 * Button to create a new {@link IDType}.
	 */
	protected Button createColumnIDTypeButton;

	/**
	 * Button that opens a dialog to define the {@link IDTypeParsingRules} for the row id type.
	 */
	protected Button defineRowIDParsingButton;

	/**
	 * Button that opens a dialog to define the {@link IDTypeParsingRules} for the column id type.
	 */
	protected Button defineColumnIDParsingButton;

	/**
	 * Group of widgets for file delimiter specification.
	 */
	protected DelimiterWidget delimiterRadioGroup;

	/**
	 * Button to determine whether the columns are homogeneous.
	 */
	protected Button homogeneousDatasetButton;

	/**
	 * Button to determine whether the columns are inhomogeneous.
	 */
	protected Button inhomogeneousDatasetButton;

	protected Label columnIDCategoryLabel;

	protected Label columnIDTypeLabel;

	protected Label rowIDCategoryLabel;

	protected Label rowIDTypeLabel;

	protected SelectAllNoneWidget selectAllNone;

	protected LoadFileWidget loadFile;

	protected LabelWidget label;

	protected Button loadDatasetDescriptionButton;

	/**
	 * Mediator for this page.
	 */
	private LoadDataSetPageMediator mediator;

	public LoadDataSetPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
		mediator = new LoadDataSetPageMediator(this, dataSetDescription);
	}

	//
	// @Override
	// public void createControl(Composite parent) {
	//
	// ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	// sc1.setLayout(new GridLayout(1, true));
	// sc1.setExpandHorizontal(true);
	// sc1.setExpandVertical(true);
	//
	//
	//
	// parentComposite.pack();
	// // parentComposite.setSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	// sc1.setMinSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	// // parentComposite.layout(true, true);
	// // sc1.layout(true, true);
	// // sc1.setMinSize(2000, 1500);
	// setControl(sc1);
	// }

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		parentComposite.setLayout(layout);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		fileComposite.setLayoutData(gd);
		GridLayout l = new GridLayout(3, false);
		l.marginWidth = 0;
		fileComposite.setLayout(l);

		// File Selection
		loadFile = new LoadFileWidget(fileComposite, "Open Data File", new ICallback<String>() {
			@Override
			public void on(String data) {
				mediator.onSelectFile(data);
			}
		}, new GridData(SWT.FILL, SWT.FILL, true, false));

		// label
		label = new LabelWidget(fileComposite, "Dataset Name");

		Group loadDatasetDescriptionGroup = new Group(fileComposite, SWT.SHADOW_ETCHED_IN);
		loadDatasetDescriptionGroup.setText("Dataset Configuration");
		loadDatasetDescriptionGroup.setLayout(new GridLayout(1, false));
		loadDatasetDescriptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		loadDatasetDescriptionButton = new Button(loadDatasetDescriptionGroup, SWT.PUSH);
		loadDatasetDescriptionButton.setText("Load Configuration");
		loadDatasetDescriptionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Load Configuration");
				// fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.xml", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				String inputFileName = fileDialog.open();
				if (inputFileName == null)
					return;
				inputFileName = inputFileName.trim();
				mediator.onLoadDatasetDescription(inputFileName);
			}
		});

		Group datasetConfigGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		datasetConfigGroup.setText("Dataset Type");
		datasetConfigGroup.setLayout(new GridLayout(3, false));
		datasetConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		Label homogenetyExplanation = new Label(datasetConfigGroup, SWT.WRAP);
		homogenetyExplanation
				.setText("Inhomogeneous datasets have a different meaning for every column and do not need to be of the same data type or identifier. For example, you could load a table where in one column contains the sex of patients while the next column contains their age.\n"
						+ "In homogeneous datasets every column is of the same type and has the same bounds. For example, in a file with normalized gene expression data all columns are of the same type and have the same bounds. This is also true for categorical data where the cateogries are global across all columns. ");
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		gd.widthHint = 400;
		homogenetyExplanation.setLayoutData(gd);

		Label chooseHomogenetyLabel = new Label(datasetConfigGroup, SWT.BOLD);
		chooseHomogenetyLabel.setText("Choose Dataset Type:");

		FontUtil.makeBold(chooseHomogenetyLabel);

		homogeneousDatasetButton = new Button(datasetConfigGroup, SWT.RADIO);
		homogeneousDatasetButton.setText("Homogeneous");

		homogeneousDatasetButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		homogeneousDatasetButton.addListener(SWT.Selection, this);
		homogeneousDatasetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.onHomogeneousDatasetSelected(true);
			}
		});

		inhomogeneousDatasetButton = new Button(datasetConfigGroup, SWT.RADIO);
		inhomogeneousDatasetButton.setText("Inhomogeneous");

		inhomogeneousDatasetButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		inhomogeneousDatasetButton.addListener(SWT.Selection, this);
		inhomogeneousDatasetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.onHomogeneousDatasetSelected(false);
			}
		});

		// Row Config
		createRowConfigPart(parentComposite);

		// Column Config
		createColumnConfigPart(parentComposite);

		// Delimiters
		delimiterRadioGroup = new DelimiterWidget(parentComposite, new ICallback<String>() {
			@Override
			public void on(String data) {
				mediator.onDelimiterChanged(data);
			}
		});

		selectAllNone = new SelectAllNoneWidget(parentComposite, new BooleanCallback() {
			@Override
			public void on(boolean selectAll) {
				mediator.onSelectAllNone(selectAll);
			}
		});

		previewTable = new PreviewTableWidget(parentComposite, new IntegerCallback() {

			@Override
			public void on(int data) {
				mediator.setDataSetChanged(true);
			}
		}, true, new INoArgumentCallback() {

			@Override
			public void on() {
				mediator.transposeFile();
			}
		});
		// , new BooleanCallback() {
		// @Override
		// public void on(boolean showAllColumns) {
		// mediator.onShowAllColumns(showAllColumns);
		// }
		// });

		mediator.guiCreated();

	}

	private void createRowConfigPart(Composite parent) {

		Group rowConfigGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		rowConfigGroup.setText("Row Configuration");
		rowConfigGroup.setToolTipText("Set the properties of the rows of the data file.");
		rowConfigGroup.setLayout(new GridLayout(2, false));
		rowConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite leftConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createIDCategoryGroup(leftConfigGroupPart, "Row Type", false);
		createIDTypeGroup(leftConfigGroupPart, false);

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(1);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 70;
		numHeaderRowsSpinner.setLayoutData(gridData);
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				mediator.numHeaderRowsSpinnerModified();
			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with Row IDs");

		columnOfRowIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnOfRowIDSpinner.setMinimum(1);
		columnOfRowIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnOfRowIDSpinner.setIncrement(1);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 70;
		columnOfRowIDSpinner.setLayoutData(gridData);
		columnOfRowIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				mediator.columnOfRowIDSpinnerModified();
			}
		});

		Composite rightConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		rightConfigGroupPart.setLayout(new GridLayout(2, false));
		rightConfigGroupPart.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		createRowIDCategoryButton = createNewIDCategoryButton(rightConfigGroupPart);
		createRowIDCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.createRowIDCategoryButtonSelected();
			}

		});
		createRowIDTypeButton = createNewIDTypeButton(rightConfigGroupPart);
		createRowIDTypeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.createRowIDTypeButtonSelected();
			}
		});

		defineRowIDParsingButton = new Button(rightConfigGroupPart, SWT.PUSH);
		defineRowIDParsingButton.setText("Define Parsing");
		defineRowIDParsingButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.onDefineRowIDParsing();
			}
		});
	}

	private Button createNewIDCategoryButton(Composite parent) {
		Button createIDCategoryButton = new Button(parent, SWT.PUSH);
		createIDCategoryButton.setText("New");
		createIDCategoryButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));

		return createIDCategoryButton;
	}

	private Button createNewIDTypeButton(Composite parent) {
		Button createIDTypeButton = new Button(parent, SWT.PUSH);
		createIDTypeButton.setText("New");

		return createIDTypeButton;
	}

	private void createColumnConfigPart(Composite parent) {

		Group columnConfigGroup = new Group(parent, SWT.NONE);
		columnConfigGroup.setText("Column Configuration");
		columnConfigGroup.setToolTipText("Set the properties of the columns of the data file.");
		columnConfigGroup.setLayout(new GridLayout(2, false));
		columnConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite leftConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createIDCategoryGroup(leftConfigGroupPart, "Column Type", true);
		createIDTypeGroup(leftConfigGroupPart, true);

		Label rowOfColumnIDLabel = new Label(leftConfigGroupPart, SWT.NONE);
		rowOfColumnIDLabel.setText("Row with Column IDs");

		rowOfColumnIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		rowOfColumnIDSpinner.setMinimum(1);
		rowOfColumnIDSpinner.setMaximum(Integer.MAX_VALUE);
		rowOfColumnIDSpinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData.widthHint = 70;
		rowOfColumnIDSpinner.setLayoutData(gridData);
		rowOfColumnIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				mediator.rowOfColumnIDSpinnerModified();
			}
		});
		// createDataPropertiesGroup(leftConfigGroupPart);

		Composite rightConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		rightConfigGroupPart.setLayout(new GridLayout(2, false));
		rightConfigGroupPart.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		createColumnIDCategoryButton = createNewIDCategoryButton(rightConfigGroupPart);
		createColumnIDCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.createColumnIDCategoryButtonSelected();
			}
		});
		createColumnIDTypeButton = createNewIDTypeButton(rightConfigGroupPart);
		createColumnIDTypeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.createColumnIDTypeButtonSelected();
			}
		});

		defineColumnIDParsingButton = new Button(rightConfigGroupPart, SWT.PUSH);
		defineColumnIDParsingButton.setText("Define Parsing");
		defineColumnIDParsingButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.onDefineColumnIDParsing();
			}
		});
	}

	private void createIDTypeGroup(Composite parent, final boolean isColumnIDTypeGroup) {
		Label idTypeLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText(isColumnIDTypeGroup ? "Column Identifier" : "Row Identifier");

		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo idCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCombo.setToolTipText("Identifiers are used to identify rows and columns and map them to other datasets or query public databases.");

		idCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		if (isColumnIDTypeGroup) {
			columnIDCombo = idCombo;
			columnIDTypeLabel = idTypeLabel;
		} else {
			rowIDCombo = idCombo;
			rowIDTypeLabel = idTypeLabel;
		}

		idCombo.addListener(SWT.Modify, this);
		idCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				mediator.idTypeComboModified(isColumnIDTypeGroup);
			}
		});
	}

	private void createIDCategoryGroup(Composite parent, String groupLabel, final boolean isColumnCategory) {
		Label idCategoryLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText(groupLabel);

		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo idCategoryCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo
				.setToolTipText("ID classes define groups of ID types that can be mapped to each other. For example a 'gene' ID class could contain multiple ID types, such as 'ensemble ID' and 'gene short name' that can be mapped to each other.");
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		idCategoryCombo.setText("<Please Select>");

		if (isColumnCategory) {
			columnIDCategoryCombo = idCategoryCombo;
			columnIDCategoryLabel = idCategoryLabel;
		} else {
			rowIDCategoryCombo = idCategoryCombo;
			rowIDCategoryLabel = idCategoryLabel;
		}

		idCategoryCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				mediator.idCategoryComboModified(isColumnCategory);
			}
		});
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	@Override
	public void fillDataSetDescription() {

		mediator.fillDataSetDescription();
	}

	@Override
	public boolean isPageComplete() {
		if (!mediator.isValidConfiguration())
			return false;

		return super.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage() {
		if (inhomogeneousDatasetButton.getSelection()) {
			return getWizard().getInhomogeneousDataPropertiesPage();
		}
		return getWizard().getDataSetTypePage();
	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

	@Override
	public void pageActivated() {
		getWizard().setChosenDataTypePage(null);
		getWizard().getContainer().updateButtons();
	}

	@Override
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		super.setDataSetDescription(dataSetDescription);
		mediator.setDataSetDescription(dataSetDescription);
	}

}
