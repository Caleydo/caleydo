/**
 * 
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.DelimiterRadioGroup;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Page for loading the dataset from file.
 * 
 * @author Christian Partl
 * 
 */
public class LoadDataSetPage extends AImportDataPage implements Listener {

	public static final String PAGE_NAME = "Load Dataset";

	public static final String PAGE_DESCRIPTION = "Specify the dataset you want to load.";

	/**
	 * Text field for the name of the dataset.
	 */
	protected Text dataSetLabelTextField;

	/**
	 * Button to specify whether the dataset is homogeneous, i.e. all columns
	 * have the same scale.
	 */
	protected Button buttonHomogeneous;

	/**
	 * Combo box to specify the {@link IDCategory} for the columns of the
	 * dataset.
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
	 * Textfield for the input file name.
	 */
	protected Text fileNameTextField;

	/**
	 * Table that displays a preview of the data of the file specified by
	 * {@link #inputFileName}.
	 */
	protected Table previewTable;

	/**
	 * Combo box to specify the row ID Type.
	 */
	protected Combo rowIDCombo;

	/**
	 * Combo box to specify the column ID Type.
	 */
	protected Combo columnIDCombo;

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
	 * Button to specify whether all columns of the data file should be shown in
	 * the {@link #previewTable}.
	 */
	protected Button showAllColumnsButton;

	/**
	 * Shows the total number columns in the data file and the number of
	 * displayed columns of the {@link #previewTable}.
	 */
	protected Label tableInfoLabel;

	/**
	 * Button to select all columns of the {@link #previewTable}.
	 */
	protected Button selectAllButton;

	/**
	 * Button to deselect all columns of the {@link #previewTable}.
	 */
	protected Button selectNoneButton;

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
	 * Group of widgets for file delimiter specification.
	 */
	protected DelimiterRadioGroup delimiterRadioGroup;

	/**
	 * Mediator for this page.
	 */
	private LoadDataSetPageMediator mediator;

	public LoadDataSetPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
		mediator = new LoadDataSetPageMediator(this, dataSetDescription);
	}

	@Override
	public void createControl(Composite parent) {

		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, true);
		parentComposite.setLayout(layout);

		// File Selection

		createFileSelectionPart(parentComposite);

		// Dataset Name

		createDataSetNamePart(parentComposite);

		// Row Config

		createRowConfigPart(parentComposite);

		// Column Config

		createColumnConfigPart(parentComposite);

		// Delimiters

		delimiterRadioGroup = new DelimiterRadioGroup();
		delimiterRadioGroup.create(parentComposite, mediator);

		Group columnSelectionGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		columnSelectionGroup.setText("Column Selection");
		columnSelectionGroup.setLayout(new GridLayout(2, false));
		columnSelectionGroup
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		selectAllButton = new Button(columnSelectionGroup, SWT.PUSH);
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.selectAllButtonPressed();
			}
		});
		selectNoneButton = new Button(columnSelectionGroup, SWT.PUSH);
		selectNoneButton.setText("Select None");
		selectNoneButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.selectNoneButtonPressed();
			}
		});

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = numGridCols;
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		// Table info

		createTableInfo(parentComposite);
		mediator.guiCreated();

		setControl(parentComposite);
	}

	private void createRowConfigPart(Composite parent) {

		Group rowConfigGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		rowConfigGroup.setText("Row Configuration");
		rowConfigGroup.setLayout(new GridLayout(2, false));
		rowConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite leftConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createIDCategoryGroup(leftConfigGroupPart, "Row ID Class", false);
		createIDTypeGroup(leftConfigGroupPart, false);

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
				mediator.numHeaderRowsSpinnerModified();
			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with Row IDs");

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
				mediator.columnOfRowIDSpinnerModified();
			}
		});

		Composite rightConfigGroupPart = new Composite(rowConfigGroup, SWT.NONE);
		rightConfigGroupPart.setLayout(new GridLayout(1, false));
		rightConfigGroupPart
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

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
	}

	private Button createNewIDCategoryButton(Composite parent) {
		Button createIDCategoryButton = new Button(parent, SWT.PUSH);
		createIDCategoryButton.setText("New");

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
		columnConfigGroup.setLayout(new GridLayout(2, false));
		columnConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite leftConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createIDCategoryGroup(leftConfigGroupPart, "Column ID Class", true);
		createIDTypeGroup(leftConfigGroupPart, true);

		Label rowOfColumnIDLabel = new Label(leftConfigGroupPart, SWT.NONE);
		rowOfColumnIDLabel.setText("Row with Column IDs");

		rowOfColumnIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		rowOfColumnIDSpinner.setMinimum(1);
		rowOfColumnIDSpinner.setMaximum(Integer.MAX_VALUE);
		rowOfColumnIDSpinner.setIncrement(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		rowOfColumnIDSpinner.setLayoutData(gridData);
		rowOfColumnIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				mediator.rowOfColumnIDSpinnerModified();
			}
		});

		createDataPropertiesGroup(leftConfigGroupPart);

		Composite rightConfigGroupPart = new Composite(columnConfigGroup, SWT.NONE);
		rightConfigGroupPart.setLayout(new GridLayout(1, false));
		rightConfigGroupPart
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

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
	}

	private void createFileSelectionPart(Composite parent) {

		Group inputFileGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input File");
		inputFileGroup.setLayout(new GridLayout(2, false));
		inputFileGroup.setLayoutData(new GridData(SWT.BEGINNING));

		Button openFileButton = new Button(inputFileGroup, SWT.PUSH);
		openFileButton.setText("Choose Data File...");

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 250;
		fileNameTextField.setLayoutData(gridData);
		fileNameTextField.setEnabled(false);
		fileNameTextField.addListener(SWT.Modify, this);

		openFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				mediator.openFileButtonPressed();
			}
		});
	}

	private void createDataSetNamePart(Composite parent) {
		Group dataSetLabelGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		dataSetLabelGroup.setText("Dataset Name");
		dataSetLabelGroup.setLayout(new GridLayout(1, false));
		dataSetLabelGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dataSetLabelTextField = new Text(dataSetLabelGroup, SWT.BORDER);
		dataSetLabelTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createIDTypeGroup(Composite parent, boolean isColumnIDTypeGroup) {
		Label idTypeLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText(isColumnIDTypeGroup ? "Column ID Type" : "Row ID Type");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo idCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		if (isColumnIDTypeGroup) {
			columnIDCombo = idCombo;
		} else {
			rowIDCombo = idCombo;
		}

		idCombo.addListener(SWT.Modify, this);
	}

	/**
	 * Creates a composite that contains the {@link #tableInfoLabel} and the
	 * {@link #showAllColumnsButton}.
	 * 
	 * @param parent
	 */
	protected void createTableInfo(Composite parent) {
		Composite tableInfoComposite = new Composite(parent, SWT.NONE);
		tableInfoComposite.setLayout(new GridLayout(3, false));
		tableInfoComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true,
				2, 1));

		tableInfoLabel = new Label(tableInfoComposite, SWT.NONE);
		tableInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

		Label separator = new Label(tableInfoComposite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData separatorGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		separatorGridData.heightHint = 16;
		separator.setLayoutData(separatorGridData);
		showAllColumnsButton = new Button(tableInfoComposite, SWT.CHECK);
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setText("Show all Columns");
		showAllColumnsButton
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		showAllColumnsButton.setEnabled(false);
		showAllColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.showAllColumnsButtonSelected();
			}

		});
	}

	private void createIDCategoryGroup(Composite parent, String groupLabel,
			final boolean isColumnCategory) {
		Label recordIDCategoryGroup = new Label(parent, SWT.SHADOW_ETCHED_IN);
		recordIDCategoryGroup.setText(groupLabel);
		recordIDCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
		Combo idCategoryCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		idCategoryCombo.setText("<Please Select>");

		if (isColumnCategory) {
			columnIDCategoryCombo = idCategoryCombo;
		} else {
			rowIDCategoryCombo = idCategoryCombo;
		}

		idCategoryCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				mediator.idCategoryComboModified(isColumnCategory);
			}
		});
	}

	private void createDataPropertiesGroup(Composite parent) {

		buttonHomogeneous = new Button(parent, SWT.CHECK);
		buttonHomogeneous.setText("Columns use same Scale");
		buttonHomogeneous.setEnabled(true);
		buttonHomogeneous.setSelection(true);
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	@Override
	public void fillDataSetDescription() {

		mediator.fillDataSetDescription();
	}

	public DataSetDescription getDataSetDescription() {
		return mediator.getDataSetDescription();
	}

	@Override
	public boolean isPageComplete() {
		if (fileNameTextField.getText().isEmpty()) {
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}

		if (rowIDCombo.getSelectionIndex() == -1) {
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}

		if (columnIDCombo.getSelectionIndex() == -1) {
			((DataImportWizard) getWizard()).setRequiredDataSpecified(false);
			return false;
		}
		((DataImportWizard) getWizard()).setRequiredDataSpecified(true);

		return super.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage() {

		return super.getNextPage();
	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}


	@Override
	public void pageActivated() {
	}

}
