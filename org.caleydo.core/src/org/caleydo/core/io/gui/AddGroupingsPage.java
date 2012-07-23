/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.File;
import java.util.ArrayList;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

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
	private List columnGroupingsList;

	/**
	 * List displaying all row groupings for the dataset.
	 */
	private List rowGroupingsList;

	/**
	 * {@link GroupingParseSpecification}s for column groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
	/**
	 * {@link GroupingParseSpecification}s for row groupings of the data.
	 */
	private ArrayList<GroupingParseSpecification> rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();

	/**
	 * {@link IDCategory} for the column groupings.
	 */
	private IDCategory columnIDCategory;

	/**
	 * {@link IDCategory} for the row groupings.
	 */
	private IDCategory rowIDCategory;

	public AddGroupingsPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, true));

		createGroupingGroup(parentComposite, "Column Groupings",
				columnGroupingSpecifications, true);
		createGroupingGroup(parentComposite, "Row Groupings", rowGroupingSpecifications,
				false);

		setControl(parentComposite);
	}

	private void createGroupingGroup(Composite parent, String groupLabel,
			final ArrayList<GroupingParseSpecification> groupingParseSpecifications,
			final boolean isColumnGrouping) {

		Group groupingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		groupingsGroup.setText(groupLabel);
		groupingsGroup.setLayout(new GridLayout(2, false));
		groupingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		if (isColumnGrouping) {
			columnGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			columnGroupingsList
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} else {
			rowGroupingsList = new List(groupingsGroup, SWT.SINGLE);
			rowGroupingsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		Composite buttonComposite = new Composite(groupingsGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		Button addGroupingButton = new Button(buttonComposite, SWT.PUSH);
		addGroupingButton.setText("Add");
		addGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		addGroupingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				ImportGroupingDialog importGroupingDialog = new ImportGroupingDialog(
						new Shell());

				String columnIDCategoryString = dataSetDescription
						.getColumnIDSpecification().getIdCategory();
				columnIDCategory = IDCategory.getIDCategory(columnIDCategoryString);
				String rowIDCategoryString = dataSetDescription.getRowIDSpecification()
						.getIdCategory();
				rowIDCategory = IDCategory.getIDCategory(rowIDCategoryString);

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

		Button editGroupingButton = new Button(buttonComposite, SWT.PUSH);
		editGroupingButton.setText("Edit");
		editGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		editGroupingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				List currentList = null;

				if (isColumnGrouping) {
					currentList = columnGroupingsList;
				} else {
					currentList = rowGroupingsList;
				}

				int groupingIndex = currentList.getSelectionIndex();
				if (groupingIndex != -1) {
					GroupingParseSpecification selectedGroupingParseSpecification = groupingParseSpecifications
							.get(groupingIndex);
					ImportGroupingDialog importGroupingDialog = new ImportGroupingDialog(
							new Shell(), selectedGroupingParseSpecification);

					String columnIDCategoryString = dataSetDescription
							.getColumnIDSpecification().getIdCategory();
					columnIDCategory = IDCategory.getIDCategory(columnIDCategoryString);
					String rowIDCategoryString = dataSetDescription
							.getRowIDSpecification().getIdCategory();
					rowIDCategory = IDCategory.getIDCategory(rowIDCategoryString);

					importGroupingDialog
							.setRowIDCategory(isColumnGrouping ? columnIDCategory
									: rowIDCategory);

					int status = importGroupingDialog.open();

					GroupingParseSpecification groupingParseSpecification = importGroupingDialog
							.getGroupingParseSpecification();

					if (status == Dialog.OK && groupingParseSpecification != null) {

						groupingParseSpecifications.remove(groupingIndex);
						groupingParseSpecifications.add(groupingIndex,
								groupingParseSpecification);

						String groupingDataSetName = groupingParseSpecification
								.getDataSourcePath().substring(
										groupingParseSpecification.getDataSourcePath()
												.lastIndexOf(File.separator) + 1,
										groupingParseSpecification.getDataSourcePath()
												.lastIndexOf("."));
						currentList.remove(groupingIndex);
						currentList.add(groupingDataSetName, groupingIndex);
					}
				}
			}

		});

		Button removeGroupingButton = new Button(buttonComposite, SWT.PUSH);
		removeGroupingButton.setText("Remove");
		removeGroupingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		removeGroupingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				List currentList = null;
				ArrayList<GroupingParseSpecification> currentGroupingParseSpecs = null;

				if (isColumnGrouping) {
					currentList = columnGroupingsList;
					currentGroupingParseSpecs = columnGroupingSpecifications;
				} else {
					currentList = rowGroupingsList;
					currentGroupingParseSpecs = rowGroupingSpecifications;
				}

				int groupingIndex = currentList.getSelectionIndex();
				if (groupingIndex != -1) {
					currentList.remove(groupingIndex);
					currentGroupingParseSpecs.remove(groupingIndex);
				}
			}

		});
	}

	@Override
	public void fillDataSetDescription() {
		dataSetDescription.setColumnGroupingSpecifications(columnGroupingSpecifications);
		dataSetDescription.setRowGroupingSpecifications(rowGroupingSpecifications);
	}

}
