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
package org.caleydo.core.io.gui.dataimport.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.gui.dataimport.CreateCategoryDialog;
import org.caleydo.core.io.gui.dataimport.widget.table.CategoryTable;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.util.color.Color;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Christian
 *
 */
public class CategoricalDataPropertiesWidget {

	/**
	 * Radio button for ordinal categories.
	 */
	protected Button ordinalButton;
	/**
	 * Radio button for nominal categories.
	 */
	protected Button nominalButton;
	/**
	 * Button to move a category up in the table.
	 */
	protected Button upButton;
	/**
	 * Button to move a category down in the table.
	 */
	protected Button downButton;
	/**
	 * Button to add a new category.
	 */
	protected Button addCategoryButton;
	/**
	 * Button to remove an existing category.
	 */
	protected Button removeCategoryButton;

	protected CategoryTable categoryTable;

	protected Composite parent;

	protected Group categoriesGroup;

	protected DataImportWizard wizard;

	public CategoricalDataPropertiesWidget(Composite parent, DataImportWizard wizard) {

		this.parent = parent;
		this.wizard = wizard;
		Group categoryTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		categoryTypeGroup.setText("Category Type");
		categoryTypeGroup.setLayout(new GridLayout(1, true));
		ordinalButton = new Button(categoryTypeGroup, SWT.RADIO);
		ordinalButton.setText("Ordinal");
		ordinalButton.setSelection(true);
		ordinalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upButton.setEnabled(true);
				downButton.setEnabled(true);
			}
		});

		nominalButton = new Button(categoryTypeGroup, SWT.RADIO);
		nominalButton.setText("Nominal");
		nominalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upButton.setEnabled(false);
				downButton.setEnabled(false);
			}
		});

		categoriesGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		categoriesGroup.setText("Categories");
		categoriesGroup.setLayout(new GridLayout(2, false));
		categoriesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite buttonComposite = new Composite(categoriesGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));
		upButton = new Button(buttonComposite, SWT.ARROW | SWT.UP);
		upButton.setLayoutData(new GridData(30, 30));
		upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedRowIndex = categoryTable.getSelectedRow();
				if (selectedRowIndex != -1 && selectedRowIndex != 0) {
					swapRows(selectedRowIndex, selectedRowIndex - 1);
					categoryTable.update();
					categoryTable.selectRow(selectedRowIndex - 1);
				}
			}

		});
		downButton = new Button(buttonComposite, SWT.ARROW | SWT.DOWN);
		downButton.setLayoutData(new GridData(30, 30));
		downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedRowIndex = categoryTable.getSelectedRow();
				if (selectedRowIndex != -1 && selectedRowIndex != categoryTable.getRowCount() - 1) {
					swapRows(selectedRowIndex, selectedRowIndex + 1);
					categoryTable.update();
					categoryTable.selectRow(selectedRowIndex + 1);
				}
			}

		});

		addCategoryButton = new Button(buttonComposite, SWT.PUSH);
		addCategoryButton.setText("Add");
		addCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addCategory();
			}

		});

		removeCategoryButton = new Button(buttonComposite, SWT.PUSH);
		removeCategoryButton.setText("Remove");
		removeCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedRowIndex = categoryTable.getSelectedRow();
				if (selectedRowIndex != -1) {
					removeCategory(selectedRowIndex);
					categoryTable.update();
				}
			}

		});

		categoryTable = new CategoryTable(categoriesGroup, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2),
				new IntegerCallback() {

					@Override
					public void on(int data) {
						if (!nominalButton.getSelection()) {
							upButton.setEnabled(true);
							downButton.setEnabled(true);
						}
						if (data == 0) {
							upButton.setEnabled(false);
						}
						if (data == categoryTable.getRowCount() - 1) {
							downButton.setEnabled(false);
						}
					}
				});

	}

	/**
	 * Updates categories according to the provided data matrix.
	 *
	 * @param dataMatrix
	 *            Matrix that is used as base to extract categories
	 * @param columnIndex
	 *            Index of a single column that is used for category extraction. Set -1 if whole matrix should be used.
	 */
	public void updateCategories(List<List<String>> dataMatrix, int columnIndex) {
		List<List<String>> categoryMatrix = extractCategoryMatrix(wizard.getFilteredDataMatrix(), columnIndex);

		categoryTable.createTableFromMatrix(categoryMatrix, 4);
		categoriesGroup.pack();
	}

	private List<List<String>> extractCategoryMatrix(List<List<String>> fileMatrix, int columnIndex) {
		Map<String, Integer> categories = new HashMap<>();

		for (List<String> row : fileMatrix) {
			if (columnIndex == -1) {
				for (String value : row) {
					addCategoryCount(categories, value);
				}
			} else {
				addCategoryCount(categories, row.get(columnIndex));
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
			categoryRow.add("0000FF");
			categoryMatrix.add(categoryRow);
		}

		return categoryMatrix;
	}

	private void addCategoryCount(Map<String, Integer> categories, String value) {
		if (!categories.containsKey(value)) {
			categories.put(value, 1);
		} else {
			categories.put(value, categories.get(value) + 1);
		}
	}

	private void addCategory() {
		List<List<String>> matrix = categoryTable.getDataMatrix();
		Set<String> categoryValues = new HashSet<>(matrix.size());
		for (List<String> row : matrix) {
			categoryValues.add(row.get(0));
		}
		CreateCategoryDialog dialog = new CreateCategoryDialog(wizard.getShell(), categoryValues);
		int status = dialog.open();

		if (status == Window.OK) {
			List<String> newCategoryRow = new ArrayList<>(4);
			newCategoryRow.add(dialog.getValue());
			newCategoryRow.add(new Integer(getNumberOfOccurrencesInFile(dialog.getValue())).toString());
			newCategoryRow.add(dialog.getName());
			newCategoryRow.add("000000");

			matrix.add(0, newCategoryRow);
			categoryTable.update();
		}
	}

	private int getNumberOfOccurrencesInFile(String value) {
		List<List<String>> dataMatrix = wizard.getFilteredDataMatrix();
		int numOccurrences = 0;
		for (List<String> row : dataMatrix) {
			for (String v : row) {
				if (v.equals(value)) {
					numOccurrences++;
				}
			}
		}
		return numOccurrences;
	}

	private void removeCategory(int rowIndex) {
		List<List<String>> matrix = categoryTable.getDataMatrix();
		matrix.remove(rowIndex);
	}

	private void swapRows(int row1Index, int row2Index) {
		List<List<String>> matrix = categoryTable.getDataMatrix();
		List<String> copyRow1 = new ArrayList<>(matrix.get(row1Index));
		matrix.set(row1Index, matrix.get(row2Index));
		matrix.set(row2Index, copyRow1);
	}

	/**
	 * @return The class description specified by the gui of this widged.
	 */
	public CategoricalClassDescription<String> getCategoricalClassDescription() {
		List<List<String>> categories = categoryTable.getDataMatrix();
		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(ordinalButton.getSelection() ? ECategoryType.ORDINAL
				: ECategoryType.NOMINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);
		for (List<String> category : categories) {
			categoricalClassDescription.addCategoryProperty(category.get(0), category.get(2),
					new Color(category.get(3)));
		}
		return categoricalClassDescription;
	}

}
