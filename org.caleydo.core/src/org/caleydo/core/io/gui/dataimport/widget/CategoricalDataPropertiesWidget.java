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
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.io.gui.dataimport.ChooseColorSchemeDialog;
import org.caleydo.core.io.gui.dataimport.CreateCategoryDialog;
import org.caleydo.core.io.gui.dataimport.widget.table.CategoryTable;
import org.caleydo.core.io.gui.dataimport.widget.table.ITableDataChangeListener;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.EColorSchemeType;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class CategoricalDataPropertiesWidget implements ITableDataChangeListener {

	/**
	 * Radio button for ordinal categories.
	 */
	protected Button ordinalButton;
	/**
	 * Radio button for nominal categories.
	 */
	protected Button nominalButton;
	// /**
	// * Button to move a category up in the table.
	// */
	// protected Button upButton;
	// /**
	// * Button to move a category down in the table.
	// */
	// protected Button downButton;
	/**
	 * Button to add a new category.
	 */
	protected Button addCategoryButton;
	/**
	 * Button to remove an existing category.
	 */
	protected Button removeCategoryButton;

	/**
	 * Button to trigger the {@link ChooseColorSchemeDialog} to select and apply a color scheme.
	 */
	protected Button applyColorSchemeButton;

	/**
	 * Combo that allows to select a neutral category
	 */
	protected Combo neutralCategoryCombo;

	/**
	 * Check box determining whether there should be a neutral category.
	 */
	protected Button existsNeutralCategoryButton;

	/**
	 * Check box to invert the order of the color scheme.
	 */
	protected Button reverseColorSchemeOrderButton;

	protected Group categoryTypeGroup;

	protected CategoryTable categoryTable;

	protected Composite parent;

	protected Group categoriesGroup;

	protected List<List<String>> datasetMatrix;

	protected int consideredColumnIndex = -1;

	protected CategoricalClassDescription<String> categoricalClassDescription;

	protected ColorBrewer currentColorScheme = ColorBrewer.RdBu;

	/**
	 * @param parent
	 */
	public CategoricalDataPropertiesWidget(Composite parent) {

		this.parent = parent;

		categoryTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		categoryTypeGroup.setText("Category Type");
		categoryTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		categoryTypeGroup.setLayout(new GridLayout(1, true));
		ordinalButton = new Button(categoryTypeGroup, SWT.RADIO);
		ordinalButton.setText("Ordinal");
		ordinalButton.setSelection(true);
		ordinalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// upButton.setEnabled(true);
				// downButton.setEnabled(true);
				existsNeutralCategoryButton.setEnabled(true);
				categoryTable.setRowsMoveable(true);
				applyColorScheme(ColorBrewer.RdBu);
				categoriesGroup.layout(true);
			}
		});

		Label ordinalLabel = new Label(categoryTypeGroup, SWT.WRAP);
		ordinalLabel
				.setText("Choose ordinal if your categories have an order. An example are population groups 'teenagers', 'adults' and 'seniors' which can be ordered by age.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		// gridData.widthHint = 500;
		ordinalLabel.setLayoutData(gridData);

		nominalButton = new Button(categoryTypeGroup, SWT.RADIO);
		nominalButton.setText("Nominal");
		nominalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// upButton.setEnabled(false);
				// downButton.setEnabled(false);
				existsNeutralCategoryButton.setEnabled(false);
				existsNeutralCategoryButton.setSelection(false);
				neutralCategoryCombo.setEnabled(false);
				categoryTable.setRowsMoveable(false);
				applyColorScheme(ColorBrewer.Set1);
				categoriesGroup.layout(true);
			}
		});
		Label nominalLabel = new Label(categoryTypeGroup, SWT.WRAP);
		nominalLabel
				.setText("Choose nominal if your categories have no order. An example is sex where no order between 'female' and 'male' is possible.");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		// gridData.widthHint = 500;
		nominalLabel.setLayoutData(gridData);

		categoriesGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		categoriesGroup.setText("Categories");
		categoriesGroup.setLayout(new GridLayout(2, false));
		categoriesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite buttonComposite = new Composite(categoriesGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));

		applyColorSchemeButton = new Button(buttonComposite, SWT.PUSH);
		applyColorSchemeButton.setText("Select Color Scheme");
		applyColorSchemeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		applyColorSchemeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseColorScheme();
			}
		});

		addCategoryButton = new Button(buttonComposite, SWT.PUSH);
		addCategoryButton.setText("Add Category");
		addCategoryButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addCategory();
			}

		});

		removeCategoryButton = new Button(buttonComposite, SWT.PUSH);
		removeCategoryButton.setText("Remove Category");
		removeCategoryButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeCategoryButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedRowIndex = categoryTable.getSelectedRow();
				if (selectedRowIndex != -1) {
					removeCategory(selectedRowIndex);
				}
			}

		});

		existsNeutralCategoryButton = new Button(buttonComposite, SWT.CHECK);
		existsNeutralCategoryButton.setText("Use Neutral Category");
		existsNeutralCategoryButton.setSelection(false);
		existsNeutralCategoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				neutralCategoryCombo.setEnabled(existsNeutralCategoryButton.getSelection());
				applyColorScheme(currentColorScheme);
			}
		});

		neutralCategoryCombo = new Combo(buttonComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		neutralCategoryCombo.setEnabled(false);
		neutralCategoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyColorScheme(currentColorScheme);
			}
		});

		reverseColorSchemeOrderButton = new Button(buttonComposite, SWT.CHECK);
		reverseColorSchemeOrderButton.setText("Reverse Color Scheme Order");
		reverseColorSchemeOrderButton.setSelection(false);
		reverseColorSchemeOrderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyColorScheme(currentColorScheme);
			}
		});

		categoryTable = new CategoryTable(categoriesGroup, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2), true);

		categoryTable.registerTableDataChangeListener(this);

		// buttonComposite = new Composite(categoriesGroup, SWT.NONE);
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
	}

	/**
	 * Updates categories according to the provided data matrix.
	 *
	 * @param dataMatrix
	 *            Matrix that is used as base to extract categories
	 * @param columnIndex
	 *            Index of a single column that is used for category extraction. Set -1 if whole matrix should be used.
	 */
	public void updateCategories(List<List<String>> datasetMatrix, int columnIndex) {
		this.datasetMatrix = datasetMatrix;
		this.consideredColumnIndex = columnIndex;
		List<List<String>> categoryMatrix = extractCategoryMatrix();

		categoryTable.createTableFromMatrix(categoryMatrix, 4);

		updateNeutralCategoryWidget(false);

		applyColorScheme(nominalButton.getSelection() ? ColorBrewer.Set1 : ColorBrewer.RdBu);

		categoriesGroup.pack();
	}

	public void updateCategories(List<List<String>> datasetMatrix, int columnIndex,
			CategoricalClassDescription<String> categoricalClassDescription) {
		this.datasetMatrix = datasetMatrix;
		this.consideredColumnIndex = columnIndex;
		List<List<String>> categoryMatrix = new ArrayList<>(categoricalClassDescription.getCategoryProperties().size());

		if (categoricalClassDescription.getCategoryType() == ECategoryType.NOMINAL) {
			nominalButton.setSelection(true);
			ordinalButton.setSelection(false);
		} else {
			ordinalButton.setSelection(true);
			nominalButton.setSelection(false);
		}

		Map<String, Integer> occurrenceMap = new HashMap<>();
		for (CategoryProperty<String> categoryProperty : categoricalClassDescription.getCategoryProperties()) {
			occurrenceMap.put(categoryProperty.getCategory(), 0);
		}

		for (List<String> row : datasetMatrix) {
			if (consideredColumnIndex == -1) {
				for (String value : row) {
					if (occurrenceMap.containsKey(value)) {
						occurrenceMap.put(value, occurrenceMap.get(value) + 1);
					}
				}
			} else {
				if (occurrenceMap.containsKey(row.get(consideredColumnIndex))) {
					occurrenceMap.put(row.get(consideredColumnIndex),
							occurrenceMap.get(row.get(consideredColumnIndex)) + 1);
				}
			}
		}

		for (CategoryProperty<String> categoryProperty : categoricalClassDescription.getCategoryProperties()) {
			List<String> category = new ArrayList<>(4);
			category.add(categoryProperty.getCategory());
			category.add(occurrenceMap.get(categoryProperty.getCategory()).toString());
			category.add(categoryProperty.getCategoryName());
			category.add(categoryProperty.getColor().getHEX());
			categoryMatrix.add(category);
		}
		categoryTable.createTableFromMatrix(categoryMatrix, 4);

		updateNeutralCategoryWidget(false);

		applyColorScheme(nominalButton.getSelection() ? ColorBrewer.Set1 : ColorBrewer.RdBu);

		categoriesGroup.pack();
	}

	private void updateNeutralCategoryWidget(boolean keepSelection) {

		String currentText = neutralCategoryCombo.getText();

		List<List<String>> categoryMatrix = categoryTable.getDataMatrix();
		neutralCategoryCombo.removeAll();
		for (List<String> categoryRow : categoryMatrix) {
			neutralCategoryCombo.add(categoryRow.get(2));
		}

		int selectionIndex = keepSelection ? neutralCategoryCombo.indexOf(currentText) : -1;
		if (selectionIndex == -1) {
			selectionIndex = (int) Math.ceil((categoryMatrix.size() / 2.0f)) - 1;
			if (selectionIndex < 0)
				selectionIndex = 0;
		}
		neutralCategoryCombo.select(selectionIndex);
	}

	private void chooseColorScheme() {
		List<ColorBrewer> colorSchemes = new ArrayList<>();

		if (nominalButton.getSelection()) {
			colorSchemes.addAll(ColorBrewer.getSets(EColorSchemeType.QUALITATIVE));
		} else {
			colorSchemes.addAll(ColorBrewer.getSets(EColorSchemeType.SEQUENTIAL));
			colorSchemes.addAll(ColorBrewer.getSets(EColorSchemeType.DIVERGING));
		}
		Collections.sort(colorSchemes);
		List<Integer> numColors = new ArrayList<>(colorSchemes.size());
		for (ColorBrewer scheme : colorSchemes) {
			numColors.add(determineSchemeSize(scheme));
		}

		ChooseColorSchemeDialog dialog = new ChooseColorSchemeDialog(parent.getShell(), colorSchemes, numColors,
				currentColorScheme);
		int status = dialog.open();

		if (status == Window.OK) {
			applyColorScheme(dialog.getSelectedColorScheme());
		}
	}

	private int determineSchemeSize(ColorBrewer scheme) {
		int numCategories = categoryTable.getRowCount();
		if (scheme.getType() != EColorSchemeType.QUALITATIVE && existsNeutralCategoryButton.getSelection()) {
			int neutralCategoryIndex = neutralCategoryCombo.getSelectionIndex();
			int numUpperCategories = numCategories - (neutralCategoryIndex + 1);
			int numLowerCategories = neutralCategoryIndex;
			int numRequiredCategories = Math.max(numUpperCategories, numLowerCategories) * 2 + 1;
			if (scheme.getSizes().contains(numRequiredCategories)) {
				return numRequiredCategories;
			}
			int maxSize = scheme.getMaxSize();
			// The scheme should contain one specific neutral color in the middle -> the number of colors has to be odd
			if (maxSize % 2.0f == 0 && scheme.getSizes().contains(maxSize - 1)) {
				maxSize = maxSize - 1;
			}
			if (numCategories > maxSize) {
				return maxSize;
			}

			int minSize = scheme.getMinSize();
			// The scheme should contain one specific neutral color in the middle -> the number of colors has to be odd
			if (minSize % 2.0f == 0 && scheme.getSizes().contains(minSize + 1)) {
				minSize = minSize + 1;
			}
			return minSize;

		} else {
			if (scheme.getSizes().contains(numCategories)) {
				return numCategories;
			}
			int maxSize = scheme.getMaxSize();
			if (numCategories > maxSize) {
				return maxSize;
			}
			return scheme.getMinSize();
		}
	}

	private List<List<String>> extractCategoryMatrix() {
		Map<String, Integer> categories = new HashMap<>();

		for (List<String> row : datasetMatrix) {
			if (consideredColumnIndex == -1) {
				for (String value : row) {
					addCategoryCount(categories, value);
				}
			} else {
				addCategoryCount(categories, row.get(consideredColumnIndex));
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

	private void applyColorScheme(ColorBrewer colorScheme) {
		List<List<String>> categoryMatrix = categoryTable.getDataMatrix();
		int numColors = determineSchemeSize(colorScheme);
		if (colorScheme.getType() == EColorSchemeType.QUALITATIVE) {
			int colorIndex = 0;
			List<java.awt.Color> colors = colorScheme.get(numColors);
			for (List<String> row : categoryMatrix) {
				Color color = (Color) Colors.of(colors.get(colorIndex));
				row.set(3, color.getHEX());
				colorIndex++;
				if (colorIndex >= colors.size()) {
					colorIndex = 0;
				}
			}
		} else {
			ColorMapper colorMapper = colorScheme.asColorMapper(numColors);
			if (existsNeutralCategoryButton.getSelection()) {
				int neutralCategoryIndex = neutralCategoryCombo.getSelectionIndex();
				float lowerCategoriesMappingValueIncrease = (neutralCategoryIndex == 0) ? 0
						: 1.0f / neutralCategoryIndex * 0.5f;
				float upperCategoriesMappingValueIncrease = (categoryMatrix.size() == categoryMatrix.size()
						- (neutralCategoryIndex + 1)) ? 0
						: 1.0f / (categoryMatrix.size() - (neutralCategoryIndex + 1)) * 0.5f;
				float currentMappingValue = 0;
				for (int i = 0; i < categoryMatrix.size(); i++) {
					if (i == neutralCategoryIndex) {
						currentMappingValue = 0.5f;
					} else if (i == categoryMatrix.size() - 1) {
						currentMappingValue = 1.0f;
					}

					List<String> row = categoryMatrix.get(i);
					row.set(3,
							colorMapper.getColorAsObject(
									reverseColorSchemeOrderButton.getSelection() ? 1.0f - currentMappingValue
											: currentMappingValue).getHEX());
					currentMappingValue += i < neutralCategoryIndex ? lowerCategoriesMappingValueIncrease
							: upperCategoriesMappingValueIncrease;
				}

			} else {
				for (int i = 0; i < categoryMatrix.size(); i++) {
					float value = (float) i / (float) (categoryMatrix.size() - 1);
					List<String> row = categoryMatrix.get(i);
					row.set(3,
							colorMapper.getColorAsObject(
									reverseColorSchemeOrderButton.getSelection() ? 1.0f - value : value).getHEX());
				}
			}
		}
		currentColorScheme = colorScheme;
		categoryTable.update();
	}

	private void addCategoryCount(Map<String, Integer> categories, String value) {
		if (!categories.containsKey(value)) {
			categories.put(value, 1);
		} else {
			categories.put(value, categories.get(value) + 1);
		}
	}

	private void addCategory() {
		List<List<String>> categoryMatrix = categoryTable.getDataMatrix();
		Set<String> categoryValues = new HashSet<>(categoryMatrix.size());
		for (List<String> row : categoryMatrix) {
			categoryValues.add(row.get(0));
		}
		CreateCategoryDialog dialog = new CreateCategoryDialog(parent.getShell(), categoryValues);
		int status = dialog.open();

		if (status == Window.OK) {
			List<String> newCategoryRow = new ArrayList<>(4);
			newCategoryRow.add(dialog.getValue());
			newCategoryRow.add(new Integer(getNumberOfOccurrencesInFile(dialog.getValue())).toString());
			newCategoryRow.add(dialog.getName());
			newCategoryRow.add("000000");

			categoryMatrix.add(0, newCategoryRow);
			categoryTable.update();
			updateNeutralCategoryWidget(true);
			applyColorScheme(currentColorScheme);
		}
	}

	private int getNumberOfOccurrencesInFile(String value) {
		int numOccurrences = 0;
		for (List<String> row : datasetMatrix) {
			if (consideredColumnIndex == -1) {
				for (String v : row) {
					if (v.equals(value)) {
						numOccurrences++;
					}
				}
			} else {
				if (value.equals(row.get(consideredColumnIndex))) {
					numOccurrences++;
				}
			}
		}
		return numOccurrences;
	}

	private void removeCategory(int rowIndex) {
		List<List<String>> categories = categoryTable.getDataMatrix();
		categories.remove(rowIndex);
		categoryTable.update();
		updateNeutralCategoryWidget(true);
		applyColorScheme(currentColorScheme);
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

	public void dispose() {
		categoryTypeGroup.dispose();
		categoriesGroup.dispose();
	}

	@Override
	public void dataChanged(EChangeType changeType) {
		updateNeutralCategoryWidget(existsNeutralCategoryButton.getSelection());
		if (changeType == EChangeType.STRUCTURAL) {
			applyColorScheme(currentColorScheme);
		}
	}

}
