/**
 * 
 */
package org.caleydo.core.io.gui.dataimport;

import java.util.ArrayList;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.util.link.LinkHandler;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 * 
 * @author Christian Partl
 * 
 */
public class ImportGroupingDialog extends AHelpButtonDialog {

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
	 * Textfield for the grouping name.
	 */
	protected Text groupingNameTextField;

	/**
	 * Mediator for this dialog.
	 */
	private ImportGroupingDialogMediator mediator;

	/**
	 * Radio group that specifies the delimiters used to parse the input file.
	 */
	protected DelimiterRadioGroup delimiterRadioGroup;

	/**
	 * Label with the {@link IDCategory} name.
	 */
	protected Label categoryIDLabel;

	/**
	 * Button to select all columns of the {@link #previewTable}.
	 */
	protected Button selectAllButton;

	/**
	 * Button to deselect all columns of the {@link #previewTable}.
	 */
	protected Button selectNoneButton;

	/**
	 * @param parentShell
	 */
	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory) {
		super(parentShell);
		mediator = new ImportGroupingDialogMediator(this, rowIDCategory);
	}

	/**
	 * @param parentShell
	 * @param groupingParseSpecification
	 *            {@link GroupingParseSpecification} that will be used to
	 *            initialize the widgets of this dialog.
	 */
	public ImportGroupingDialog(Shell parentShell,
			GroupingParseSpecification groupingParseSpecification,
			IDCategory rowIDCategory) {
		super(parentShell);
		mediator = new ImportGroupingDialogMediator(this, groupingParseSpecification,
				rowIDCategory);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Grouping");
	}

	@Override
	protected void okPressed() {

		if (!mediator.okPressed())
			return;

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

				mediator.openFileButtonPressed();
			}
		});

		createRowConfigPart(parentComposite);

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
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, numGridCols, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);

		createTableInfo(parentComposite);

		mediator.guiCreated();
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
		categoryIDLabel = new Label(leftConfigGroupPart, SWT.NONE);

		Label idTypeLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Row ID Type");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		rowIDCombo = new Combo(leftConfigGroupPart, SWT.DROP_DOWN | SWT.READ_ONLY);
		rowIDCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(0);
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
				mediator.columnOfRowIDSpinnerModified();
			}
		});

	}

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return mediator.getGroupingParseSpecification();
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
		tableInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label separator = new Label(tableInfoComposite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData separatorGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		separatorGridData.heightHint = 16;
		separator.setLayoutData(separatorGridData);
		showAllColumnsButton = new Button(tableInfoComposite, SWT.CHECK);
		showAllColumnsButton.setText("Show all Columns");
		showAllColumnsButton.setSelection(false);
		showAllColumnsButton.setEnabled(false);
		showAllColumnsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mediator.showAllColumnsButtonPressed();
			}

		});
	}

	@Override
	protected void helpPressed() {
		LinkHandler
				.openLink("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
	}

}
