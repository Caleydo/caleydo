/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * Page that is used to specify groupings for a dataset.
 *
 * @author Christian Partl
 *
 */
public class AddGroupingsPage extends AImportDataPage {

	public static final String PAGE_NAME = "Add Groupings";

	public static final String PAGE_DESCRIPTION = "Add groupings for columns and rows.";

	/**
	 * List displaying all column groupings for the dataset.
	 */
	protected List columnGroupingsList;

	/**
	 * List displaying all row groupings for the dataset.
	 */
	protected List rowGroupingsList;

	/**
	 * Button to add a grouping for columns.
	 */
	protected Button addColumnGroupingButton;

	/**
	 * Button to add a grouping for rows.
	 */
	protected Button addRowGroupingButton;

	/**
	 * Button to edit a grouping for columns.
	 */
	protected Button editColumnGroupingButton;

	/**
	 * Button to edit a grouping for rows.
	 */
	protected Button editRowGroupingButton;

	/**
	 * Button to remove a grouping for columns.
	 */
	protected Button removeColumnGroupingButton;

	/**
	 * Button to remove a grouping for rows.
	 */
	protected Button removeRowGroupingButton;

	protected Group columnGroupingsGroup;

	protected Group rowGroupingsGroup;

	private AddGroupingsPageMediator mediator;

	public AddGroupingsPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
		mediator = new AddGroupingsPageMediator(this, dataSetDescription);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, true));

		Label groupingDescriptionLabel = new Label(parentComposite, SWT.WRAP);
		groupingDescriptionLabel
				.setText("Add external files that specify groupings for rows or columns of the dataset.");
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = 200;
		groupingDescriptionLabel.setLayoutData(gridData);

		createGroupingGroup(parentComposite, "Column Groupings", true);
		createGroupingGroup(parentComposite, "Row Groupings", false);

		mediator.guiCreated();
	}

	private void createGroupingGroup(Composite parent, String groupLabel,
			final boolean isColumnGrouping) {

		Group groupingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupingsGroup.setText(groupLabel);
		groupingsGroup.setLayout(new GridLayout(2, false));
		groupingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite buttonComposite = new Composite(groupingsGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		Button addGroupingButton = new Button(buttonComposite, SWT.PUSH);
		addGroupingButton.setText("Add");
		addGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Button editGroupingButton = new Button(buttonComposite, SWT.PUSH);
		editGroupingButton.setText("Edit");
		editGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Button removeGroupingButton = new Button(buttonComposite, SWT.PUSH);
		removeGroupingButton.setText("Remove");
		removeGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		if (isColumnGrouping) {

			columnGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			columnGroupingsList
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			columnGroupingsList.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.columnGroupingsListSelected();
				}
			});

			addColumnGroupingButton = addGroupingButton;
			addColumnGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.addColumnGroupingButtonSelected();
				}
			});
			editColumnGroupingButton = editGroupingButton;
			editColumnGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.editColumnGroupingButtonSelected();
				}
			});
			removeColumnGroupingButton = removeGroupingButton;
			removeColumnGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.removeColumnGroupingButtonSelected();
				}
			});
		} else {

			rowGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			rowGroupingsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			rowGroupingsList.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.rowGroupingsListSelected();
				}
			});

			addRowGroupingButton = addGroupingButton;
			addRowGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.addRowGroupingButtonSelected();
				}
			});
			editRowGroupingButton = editGroupingButton;
			editRowGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.editRowGroupingButtonSelected();
				}
			});
			removeRowGroupingButton = removeGroupingButton;
			removeRowGroupingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mediator.removeRowGroupingButtonSelected();
				}
			});
		}

		if (isColumnGrouping) {
			columnGroupingsGroup = groupingsGroup;
		} else {
			rowGroupingsGroup = groupingsGroup;
		}
	}

	@Override
	public void fillDataSetDescription() {
		mediator.fillDataSetDescription();
	}

	@Override
	public void pageActivated() {
		mediator.pageActivated();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return getWizard().getChosenDataTypePage();
	}

}
