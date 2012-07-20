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

	/**
	 * @param pageName
	 */
	protected AddGroupingsPage(String pageName, DataSetDescription dataSetDescription) {
		super(pageName, dataSetDescription);
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
				
				String columnIDCategoryString = dataSetDescription.getColumnIDSpecification()
						.getIdCategory();
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
	}

	@Override
	public void fillDataSetDescription() {
		dataSetDescription.setColumnGroupingSpecifications(columnGroupingSpecifications);
		dataSetDescription.setRowGroupingSpecifications(rowGroupingSpecifications);
	}

}
