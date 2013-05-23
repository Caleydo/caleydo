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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.DataTranspositionWidget;
import org.caleydo.core.io.gui.dataimport.widget.table.CategoryTable;
import org.caleydo.core.util.color.Color;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Christian
 *
 */
public class CategoricalDataPropertiesPage extends AImportDataPage {

	public static final String PAGE_NAME = "Categorical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the categorical dataset.";

	/**
	 * Radio button for ordinal categories.
	 */
	protected Button ordinalButton;
	/**
	 * Radio button for nominal categories.
	 */
	protected Button nominalButton;

	protected CategoryTable categoryTable;

	protected DataTranspositionWidget dataTranspositionWidget;

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
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));
		Group categoryTypeGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		categoryTypeGroup.setText("Category Type");
		categoryTypeGroup.setLayout(new GridLayout(1, true));
		ordinalButton = new Button(categoryTypeGroup, SWT.RADIO);
		ordinalButton.setText("Ordinal");
		ordinalButton.setSelection(true);

		nominalButton = new Button(categoryTypeGroup, SWT.RADIO);
		nominalButton.setText("Nominal");

		Group categoriesGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		categoriesGroup.setText("Categories");
		categoriesGroup.setLayout(new GridLayout(1, true));
		categoriesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		categoryTable = new CategoryTable(categoriesGroup);

		dataTranspositionWidget = new DataTranspositionWidget(parentComposite, (DataImportWizard) getWizard(),
				dataSetDescription.isTransposeMatrix());

		setControl(parentComposite);
	}

	@Override
	public void fillDataSetDescription() {

		List<List<String>> categories = categoryTable.getDataMatrix();
		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(ordinalButton.getSelection() ? ECategoryType.ORDINAL
				: ECategoryType.NOMINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);
		for (List<String> category : categories) {
			categoricalClassDescription.addCategoryProperty(category.get(0), category.get(2),
					new Color("000000"));
		}

		dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

		dataSetDescription.getDataDescription().setCategoricalClassDescription(categoricalClassDescription);

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
		List<List<String>> categoryMatrix = extractCategoryMatrix(wizard.getFilteredDataMatrix());

		categoryTable.createTableFromMatrix(categoryMatrix, 4);

		dataTranspositionWidget.update();

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();

	}

	private List<List<String>> extractCategoryMatrix(List<List<String>> fileMatrix) {
		Map<String, Integer> categories = new HashMap<>();

		for (List<String> row : fileMatrix) {
			for (String value : row) {
				if (!categories.containsKey(value)) {
					categories.put(value, 1);
				} else {
					categories.put(value, categories.get(value) + 1);
				}
			}
		}

		List<String> categoryValues = new ArrayList<>(categories.keySet());
		Collections.sort(categoryValues);

		List<List<String>> categoryMatrix = new ArrayList<>(categoryValues.size());

		for (String categoryValue : categoryValues) {
			List<String> categoryRow = new ArrayList<>(4);
			// The data value
			categoryRow.add(categoryValue);
			// Number of occurrences
			categoryRow.add(categories.get(categoryValue).toString());
			// The category name (initially same as value)
			categoryRow.add(categoryValue);
			// The color of the category
			categoryRow.add("0");
			categoryMatrix.add(categoryRow);
		}

		return categoryMatrix;
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
