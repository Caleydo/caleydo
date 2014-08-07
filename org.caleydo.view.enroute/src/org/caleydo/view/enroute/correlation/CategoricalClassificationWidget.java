/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.HashSet;

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

	protected CategoricalDataClassifier classifier = new CategoricalDataClassifier(new HashSet<>(), new HashSet<>());

	/**
	 * @param parent
	 * @param style
	 */
	public CategoricalClassificationWidget(Composite parent, int style, java.util.List<Color> categoryColors) {
		super(parent, style, categoryColors);

		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		moveToCategory1Button = new Button(this, SWT.PUSH);
		moveToCategory1Button.setText("Move Up");
		moveToCategory1Button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		moveToCategory1Button.setEnabled(false);

		moveToCategory2Button = new Button(this, SWT.PUSH);
		moveToCategory2Button.setText("Move Down");
		moveToCategory2Button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		moveToCategory2Button.setEnabled(false);

		category1List = createCategoryList(this, new SimpleCategory("top", Color.CYAN));
		category1List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory2Button.setEnabled(true);
				moveToCategory1Button.setEnabled(false);
			}
		});

		category2List = createCategoryList(this, new SimpleCategory("top", Color.MAGENTA));
		category2List.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToCategory1Button.setEnabled(true);
				moveToCategory2Button.setEnabled(false);
			}
		});
	}

	protected List createCategoryList(Composite parent, SimpleCategory category) {
		List list = new List(parent, SWT.SHADOW_ETCHED_IN);
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
		// TODO Auto-generated method stub

	}

}
