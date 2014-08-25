/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.util.color.Color;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class CategoricalClassificationWidget extends AClassificationWidget {

	protected List category1List;
	protected List category2List;

	protected Button moveToCategory1Button;
	protected Button moveToCategory2Button;

	protected java.util.List<Text> classLabels = new ArrayList<>();

	private Button useCustomCategoryNamesButton;

	protected CategoricalDataClassifier classifier;

	protected CategoricalClassDescription<?> classDescription;

	/**
	 * @param parent
	 * @param style
	 */
	public CategoricalClassificationWidget(Composite parent, int style, java.util.List<Color> categoryColors) {
		super(parent, style, categoryColors);
		classifier = new CategoricalDataClassifier(new HashSet<>(), new HashSet<>(), categoryColors.get(0),
				categoryColors.get(1), "class 1", "class 2", null);

		GridLayout layout = new GridLayout(3, false);
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite class1Composite = new Composite(this, SWT.None);
		class1Composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		class1Composite.setLayout(new GridLayout(2, false));

		addClassLabel(class1Composite, categoryColors.get(0), "class 1");
		category1List = createCategoryList(class1Composite, new SimpleCategory("class 1", categoryColors.get(0)));
		category1List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory2Button.setEnabled(true);
				moveToCategory1Button.setEnabled(false);
				category2List.deselectAll();
			}
		});

		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonComposite.setLayout(new GridLayout(1, false));

		moveToCategory2Button = new Button(buttonComposite, SWT.PUSH);
		moveToCategory2Button.setText(">");
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = 40;
		moveToCategory2Button.setLayoutData(gd);
		moveToCategory2Button.setEnabled(false);
		moveToCategory2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedCategories(category1List, category2List);
				createClassifierFromLists();
			}
		});

		moveToCategory1Button = new Button(buttonComposite, SWT.PUSH);
		moveToCategory1Button.setText("<");
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = 40;
		moveToCategory1Button.setLayoutData(gd);
		moveToCategory1Button.setEnabled(false);
		moveToCategory1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedCategories(category2List, category1List);
				createClassifierFromLists();
			}
		});

		Composite class2Composite = new Composite(this, SWT.None);
		class2Composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		class2Composite.setLayout(new GridLayout(2, false));

		addClassLabel(class2Composite, categoryColors.get(1), "class 2");
		category2List = createCategoryList(class2Composite, new SimpleCategory("class 2", categoryColors.get(1)));
		category2List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory1Button.setEnabled(true);
				moveToCategory2Button.setEnabled(false);
				category1List.deselectAll();
			}
		});

		useCustomCategoryNamesButton = new Button(this, SWT.CHECK);
		useCustomCategoryNamesButton.setText("Use custom class names");
		useCustomCategoryNamesButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1));
		useCustomCategoryNamesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Text t : classLabels) {
					t.setEditable(useCustomCategoryNamesButton.getSelection());
				}
			}
		});
	}


	protected void addClassLabel(Composite parent, Color color, String text) {
		CLabel preview = new CLabel(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.widthHint = 25;
		gridData.heightHint = 25;
		preview.setLayoutData(gridData);
		org.eclipse.swt.graphics.Color c = color.getSWTColor(Display.getCurrent());
		preview.setBackground(c);
		preview.update();
		colorRegistry.add(c);

		Text l = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = 200;
		l.setLayoutData(gridData);
		l.setText(text);
		l.setEditable(false);
		l.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (useCustomCategoryNamesButton.getSelection()) {
					createClassifierFromLists();
				}
			}
		});
		classLabels.add(l);
	}

	protected void moveSelectedCategories(List source, List target) {
		for (int index : source.getSelectionIndices()) {
			String item = source.getItem(index);
			target.add(item);
			target.setData(item, source.getData(item));
		}
		source.remove(source.getSelectionIndices());
		updateCategoryLabels();
	}

	private void updateCategoryLabels() {
		if (!useCustomCategoryNamesButton.getSelection()) {
			classLabels.get(0).setText(getCategoryNameFromList(category1List));
			classLabels.get(1).setText(getCategoryNameFromList(category2List));
		}
	}

	private String getCategoryNameFromList(List list) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < list.getItemCount(); i++) {
			String item = list.getItem(i);
			b.append(item);
			if (i < list.getItemCount() - 1)
				b.append(", ");
		}

		return b.toString();
	}

	protected List createCategoryList(Composite parent, SimpleCategory category) {
		List list = new List(parent, SWT.MULTI | SWT.BORDER);
		// org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
		// list.setBackground(c);
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

				updateCategoryLabels();
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
				getCategoriesFromList(category2List), categoryColors.get(0), categoryColors.get(1), classLabels.get(0)
						.getText(), classLabels.get(1).getText(), classDescription);
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
