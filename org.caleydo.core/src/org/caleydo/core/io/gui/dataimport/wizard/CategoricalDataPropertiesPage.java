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
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;

import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.CategoricalDataPropertiesWidget;
import org.caleydo.core.io.gui.dataimport.widget.DataTranspositionWidget;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Page to specify categorical properties.
 *
 * @author Christian Partl
 *
 */
public class CategoricalDataPropertiesPage extends AImportDataPage {

	public static final String PAGE_NAME = "Categorical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the categorical dataset.";

	// /**
	// * Radio button for ordinal categories.
	// */
	// protected Button ordinalButton;
	// /**
	// * Radio button for nominal categories.
	// */
	// protected Button nominalButton;
	// /**
	// * Button to move a category up in the table.
	// */
	// protected Button upButton;
	// /**
	// * Button to move a category down in the table.
	// */
	// protected Button downButton;
	// /**
	// * Button to add a new category.
	// */
	// protected Button addCategoryButton;
	// /**
	// * Button to remove an existing category.
	// */
	// protected Button removeCategoryButton;
	//
	// protected CategoryTable categoryTable;

	protected DataTranspositionWidget dataTranspositionWidget;

	protected Composite parentComposite;

	// protected Group categoriesGroup;

	protected CategoricalDataPropertiesWidget categoricalDataWidget;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected CategoricalDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		categoricalDataWidget = new CategoricalDataPropertiesWidget(parentComposite, (DataImportWizard) getWizard());
		// Group categoryTypeGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		// categoryTypeGroup.setText("Category Type");
		// categoryTypeGroup.setLayout(new GridLayout(1, true));
		// ordinalButton = new Button(categoryTypeGroup, SWT.RADIO);
		// ordinalButton.setText("Ordinal");
		// ordinalButton.setSelection(true);
		// ordinalButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// upButton.setEnabled(true);
		// downButton.setEnabled(true);
		// }
		// });
		//
		// nominalButton = new Button(categoryTypeGroup, SWT.RADIO);
		// nominalButton.setText("Nominal");
		// nominalButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// upButton.setEnabled(false);
		// downButton.setEnabled(false);
		// }
		// });
		//
		// categoriesGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		// categoriesGroup.setText("Categories");
		// categoriesGroup.setLayout(new GridLayout(2, false));
		// categoriesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		//
		// Composite buttonComposite = new Composite(categoriesGroup, SWT.NONE);
		// buttonComposite.setLayout(new GridLayout(1, true));
		// upButton = new Button(buttonComposite, SWT.ARROW | SWT.UP);
		// upButton.setLayoutData(new GridData(30, 30));
		// upButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// int selectedRowIndex = categoryTable.getSelectedRow();
		// if (selectedRowIndex != -1 && selectedRowIndex != 0) {
		// swapRows(selectedRowIndex, selectedRowIndex - 1);
		// categoryTable.update();
		// categoryTable.selectRow(selectedRowIndex - 1);
		// }
		// }
		//
		// });
		// downButton = new Button(buttonComposite, SWT.ARROW | SWT.DOWN);
		// downButton.setLayoutData(new GridData(30, 30));
		// downButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// int selectedRowIndex = categoryTable.getSelectedRow();
		// if (selectedRowIndex != -1 && selectedRowIndex != categoryTable.getRowCount() - 1) {
		// swapRows(selectedRowIndex, selectedRowIndex + 1);
		// categoryTable.update();
		// categoryTable.selectRow(selectedRowIndex + 1);
		// }
		// }
		//
		// });
		//
		// addCategoryButton = new Button(buttonComposite, SWT.PUSH);
		// addCategoryButton.setText("Add");
		// addCategoryButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// addCategory();
		// }
		//
		// });
		//
		// removeCategoryButton = new Button(buttonComposite, SWT.PUSH);
		// removeCategoryButton.setText("Remove");
		// removeCategoryButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// int selectedRowIndex = categoryTable.getSelectedRow();
		// if (selectedRowIndex != -1) {
		// removeCategory(selectedRowIndex);
		// categoryTable.update();
		// }
		// }
		//
		// });
		//
		// categoryTable = new CategoryTable(categoriesGroup, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2),
		// new IntegerCallback() {
		//
		// @Override
		// public void on(int data) {
		// if (!nominalButton.getSelection()) {
		// upButton.setEnabled(true);
		// downButton.setEnabled(true);
		// }
		// if (data == 0) {
		// upButton.setEnabled(false);
		// }
		// if (data == categoryTable.getRowCount() - 1) {
		// downButton.setEnabled(false);
		// }
		// }
		// });

		dataTranspositionWidget = new DataTranspositionWidget(parentComposite, (DataImportWizard) getWizard(),
				dataSetDescription.isTransposeMatrix());

		setControl(parentComposite);
	}

	@Override
	public void fillDataSetDescription() {

		dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

		dataSetDescription.getDataDescription().setCategoricalClassDescription(
				categoricalDataWidget.getCategoricalClassDescription());

		ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
		DataImportWizard wizard = (DataImportWizard) getWizard();

		for (Integer selected : wizard.getSelectedColumns()) {
			int columnIndex = selected.intValue();
			if (columnIndex == dataSetDescription.getColumnOfRowIds())
				continue;
			inputPattern.add(new ColumnDescription(columnIndex, dataSetDescription.getDataDescription()));
		}

		dataSetDescription.setParsingPattern(inputPattern);
	}

	@Override
	public void pageActivated() {

		DataImportWizard wizard = (DataImportWizard) getWizard();

		categoricalDataWidget.updateCategories(wizard.getFilteredDataMatrix(), -1);

		dataTranspositionWidget.update();

		// categoryTable.update();
		parentComposite.layout(true);

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();

	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((DataImportWizard) getWizard()).getDataSetTypePage();
	}

	@Override
	public IWizardPage getNextPage() {
		return ((DataImportWizard) getWizard()).getAddGroupingsPage();
	}

}
