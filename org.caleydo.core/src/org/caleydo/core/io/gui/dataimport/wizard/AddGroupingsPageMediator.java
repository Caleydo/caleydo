/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.gui.dataimport.ImportGroupingDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Mediator for {@link AddGroupingsPage}. This class is responsible for setting the states of all widgets of the page
 * and triggering actions according to different events that occur in the page.
 *
 *
 * @author Christian Partl
 *
 */
public class AddGroupingsPageMediator {

	/**
	 * {@link GroupingParseSpecification}s for column groupings of the data.
	 */
	private List<GroupingParseSpecification> columnGroupingSpecifications = new ArrayList<GroupingParseSpecification>();
	/**
	 * {@link GroupingParseSpecification}s for row groupings of the data.
	 */
	private List<GroupingParseSpecification> rowGroupingSpecifications = new ArrayList<GroupingParseSpecification>();

	/**
	 * {@link IDCategory} for the column groupings.
	 */
	private IDCategory columnIDCategory;

	/**
	 * {@link IDCategory} for the row groupings.
	 */
	private IDCategory rowIDCategory;

	/**
	 * Page this class serves as mediator for.
	 */
	private AddGroupingsPage page;

	/**
	 * {@link DataSetDescription} of the dataset groupings are imported for by the {@link #page}.
	 */
	private DataSetDescription dataSetDescription;

	public AddGroupingsPageMediator(AddGroupingsPage page, DataSetDescription dataSetDescription) {
		this.page = page;
		this.dataSetDescription = dataSetDescription;
	}

	/**
	 * Opens up an {@link ImportGroupingDialog} to add a column grouping.
	 */
	public void addColumnGroupingButtonSelected() {
		addGrouping(columnIDCategory, columnGroupingSpecifications, page.columnGroupingsList);
	}

	/**
	 * Opens up an {@link ImportGroupingDialog} to add a row grouping.
	 */
	public void addRowGroupingButtonSelected() {
		addGrouping(rowIDCategory, rowGroupingSpecifications, page.rowGroupingsList);
	}

	private void addGrouping(IDCategory idCategory, List<GroupingParseSpecification> groupingParseSpecifications,
			org.eclipse.swt.widgets.List groupingList) {
		ImportGroupingDialog importGroupingDialog = new ImportGroupingDialog(new Shell(), idCategory);
		GroupingParseSpecification groupingParseSpecification = importGroupingDialog.call();
		if (groupingParseSpecification != null) {
			groupingParseSpecifications.add(groupingParseSpecification);
			groupingList.add(groupingParseSpecification.getGroupingName());
		}
	}

	/**
	 * Opens up an {@link ImportGroupingDialog} to edit a selected column grouping.
	 */
	public void editColumnGroupingButtonSelected() {
		editGrouping(columnIDCategory, columnGroupingSpecifications, page.columnGroupingsList);
	}

	/**
	 * Opens up an {@link ImportGroupingDialog} to edit a selected row grouping.
	 */
	public void editRowGroupingButtonSelected() {
		editGrouping(rowIDCategory, rowGroupingSpecifications, page.rowGroupingsList);
	}

	private void editGrouping(IDCategory idCategory, List<GroupingParseSpecification> groupingParseSpecifications,
			org.eclipse.swt.widgets.List groupingList) {

		int groupingIndex = groupingList.getSelectionIndex();
		if (groupingIndex != -1) {
			GroupingParseSpecification selectedGroupingParseSpecification = groupingParseSpecifications
					.get(groupingIndex);

			ImportGroupingDialog importGroupingDialog = new ImportGroupingDialog(new Shell(), idCategory,
					selectedGroupingParseSpecification);
			GroupingParseSpecification groupingParseSpecification = importGroupingDialog.call();
			if (groupingParseSpecification != null) {
				groupingParseSpecifications.remove(groupingIndex);
				groupingParseSpecifications.add(groupingIndex, groupingParseSpecification);

				groupingList.remove(groupingIndex);
				groupingList.add(groupingParseSpecification.getGroupingName(), groupingIndex);
			}
		}
	}

	/**
	 * Removes a selected column grouping.
	 */
	public void removeColumnGroupingButtonSelected() {
		removeGrouping(columnGroupingSpecifications, page.columnGroupingsList);
	}

	/**
	 * Removes a selected row grouping.
	 */
	public void removeRowGroupingButtonSelected() {
		removeGrouping(rowGroupingSpecifications, page.rowGroupingsList);
	}

	private void removeGrouping(List<GroupingParseSpecification> groupingParseSpecifications,
			org.eclipse.swt.widgets.List groupingList) {

		int groupingIndex = groupingList.getSelectionIndex();
		if (groupingIndex != -1) {
			groupingList.remove(groupingIndex);
			groupingParseSpecifications.remove(groupingIndex);
		}
		columnGroupingsListSelected();
		rowGroupingsListSelected();
	}

	public void columnGroupingsListSelected() {
		boolean enableButtons = page.columnGroupingsList.getSelectionIndex() != -1;

		page.editColumnGroupingButton.setEnabled(enableButtons);
		page.removeColumnGroupingButton.setEnabled(enableButtons);

	}

	public void rowGroupingsListSelected() {
		boolean enableButtons = page.rowGroupingsList.getSelectionIndex() != -1;

		page.editRowGroupingButton.setEnabled(enableButtons);
		page.removeRowGroupingButton.setEnabled(enableButtons);
	}

	public void guiCreated() {
		columnGroupingsListSelected();
		rowGroupingsListSelected();
	}

	private void enableColumnGrouping(boolean enabled) {
		page.columnGroupingsGroup.setEnabled(enabled);
		page.addColumnGroupingButton.setEnabled(enabled);
		page.editColumnGroupingButton.setEnabled(enabled);
		page.removeColumnGroupingButton.setEnabled(enabled);
		page.columnGroupingsList.setEnabled(enabled);
	}

	public void pageActivated() {
		if (dataSetDescription.getColumnIDSpecification() == null) {
			enableColumnGrouping(false);
			page.columnGroupingsList.removeAll();
			columnGroupingSpecifications.clear();
		} else {
			String columnIDCategoryString = dataSetDescription.getColumnIDSpecification().getIdCategory();
			columnIDCategory = IDCategory.getIDCategory(columnIDCategoryString);
			enableColumnGrouping(true);
		}
		String rowIDCategoryString = dataSetDescription.getRowIDSpecification().getIdCategory();
		rowIDCategory = IDCategory.getIDCategory(rowIDCategoryString);

		if (dataSetDescription.getColumnGroupingSpecifications() != null) {
			columnGroupingSpecifications = dataSetDescription.getColumnGroupingSpecifications();
			page.columnGroupingsList.removeAll();
			for (GroupingParseSpecification groupingParseSpecification : columnGroupingSpecifications) {
				page.columnGroupingsList.add(groupingParseSpecification.getGroupingName());
			}
		} else {
			// keep grouping specifications in datasetdesciption synchronized
			dataSetDescription.setColumnGroupingSpecifications(columnGroupingSpecifications);
		}

		if (dataSetDescription.getRowGroupingSpecifications() != null) {
			rowGroupingSpecifications = dataSetDescription.getRowGroupingSpecifications();
			page.rowGroupingsList.removeAll();
			for (GroupingParseSpecification groupingParseSpecification : rowGroupingSpecifications) {
				page.rowGroupingsList.add(groupingParseSpecification.getGroupingName());
			}
		} else {
			// keep grouping specifications in datasetdesciption synchronized
			dataSetDescription.setRowGroupingSpecifications(rowGroupingSpecifications);
		}

		guiCreated();

	}

	public void fillDataSetDescription() {
		dataSetDescription.setColumnGroupingSpecifications(columnGroupingSpecifications);
		dataSetDescription.setRowGroupingSpecifications(rowGroupingSpecifications);
	}

}
