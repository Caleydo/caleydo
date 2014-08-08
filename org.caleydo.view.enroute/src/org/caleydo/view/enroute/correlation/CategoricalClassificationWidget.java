/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.util.color.Color;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

/**
 * @author Christian
 *
 */
public class CategoricalClassificationWidget extends AClassificationWidget {

	protected List category1List;
	protected List category2List;

	protected Button moveToCategory1Button;
	protected Button moveToCategory2Button;

	protected CategoricalDataClassifier classifier;

	protected CategoricalClassDescription<?> classDescription;

	/**
	 * @param parent
	 * @param style
	 */
	public CategoricalClassificationWidget(Composite parent, int style, java.util.List<Color> categoryColors) {
		super(parent, style, categoryColors);
		classifier = new CategoricalDataClassifier(new HashSet<>(), new HashSet<>(), categoryColors.get(0),
				categoryColors.get(1), null);

		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		category1List = createCategoryList(this, new SimpleCategory("class 1", categoryColors.get(0)));
		category1List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory2Button.setEnabled(true);
				moveToCategory1Button.setEnabled(false);
				category2List.deselectAll();
			}
		});

		moveToCategory1Button = new Button(this, SWT.PUSH);
		moveToCategory1Button.setText("Move Up");
		moveToCategory1Button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		moveToCategory1Button.setEnabled(false);
		moveToCategory1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedCategories(category2List, category1List);
				createClassifierFromLists();
			}
		});

		moveToCategory2Button = new Button(this, SWT.PUSH);
		moveToCategory2Button.setText("Move Down");
		moveToCategory2Button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		moveToCategory2Button.setEnabled(false);
		moveToCategory2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedCategories(category1List, category2List);
				createClassifierFromLists();
			}
		});

		category2List = createCategoryList(this, new SimpleCategory("class 2", categoryColors.get(1)));
		category2List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory1Button.setEnabled(true);
				moveToCategory2Button.setEnabled(false);
				category1List.deselectAll();
			}
		});
	}

	protected void moveSelectedCategories(List source, List target) {
		for (int index : source.getSelectionIndices()) {
			String item = source.getItem(index);
			target.add(item);
			target.setData(item, source.getData(item));
		}
		source.remove(source.getSelectionIndices());
	}

	protected List createCategoryList(Composite parent, SimpleCategory category) {
		List list = new List(parent, SWT.MULTI | SWT.BORDER);
		org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
		list.setBackground(c);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		return list;
	}

	@Override
	public IDataClassifier getClassifier() {
		return classifier;
	}

	@Override
	public void updateData(DataCellInfo info) {
		Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
				info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

		if (description instanceof CategoricalClassDescription) {
			CategoricalClassDescription<?> classDesc = (CategoricalClassDescription<?>) description;
			if (classDesc != classDescription) {
				classDescription = classDesc;
				category1List.removeAll();
				category2List.removeAll();

				int categoryHalfIndex = (classDesc.getCategoryProperties().size() / 2) - 1;

				addCategoriesToList(category1List, 0, categoryHalfIndex);
				addCategoriesToList(category2List, categoryHalfIndex + 1, classDesc.getCategoryProperties().size() - 1);

				createClassifierFromLists();

			}
		}
	}

	private void addCategoriesToList(List list, int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			CategoryProperty<?> property = classDescription.getCategoryProperties().get(i);
			list.add(property.getCategoryName());
			list.setData(property.getCategoryName(), property.getCategory());
		}
	}

	private void createClassifierFromLists() {
		classifier = new CategoricalDataClassifier(getCategoriesFromList(category1List),
				getCategoriesFromList(category2List), categoryColors.get(0), categoryColors.get(1), classDescription);
		notifyOfClassifierChange();
	}

	private Set<Object> getCategoriesFromList(List list) {
		Set<Object> categories = new HashSet<>();

		for (String item : list.getItems()) {
			categories.add(list.getData(item));
		}

		return categories;
	}
}
