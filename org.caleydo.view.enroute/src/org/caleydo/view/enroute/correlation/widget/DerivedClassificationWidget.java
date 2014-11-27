/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.IIDClassifier;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class DerivedClassificationWidget extends Composite {

	protected Set<org.eclipse.swt.graphics.Color> colorRegistry = new HashSet<>();
	protected IIDClassifier classifier;

	protected java.util.List<Label> classLabels;
	protected java.util.List<CLabel> colorLabels;

	/**
	 * @param parent
	 * @param style
	 */
	public DerivedClassificationWidget(Composite parent) {
		super(parent, SWT.NONE);
		classifier = new SimpleIDClassifier(new HashSet<>(), new HashSet<>(), null,
				new SimpleCategory("", Color.BLACK), new SimpleCategory("", Color.BLACK));

		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		classLabels = new ArrayList<>(classifier.getDataClasses().size());
		colorLabels = new ArrayList<>(classifier.getDataClasses().size());

		for (SimpleCategory category : classifier.getDataClasses()) {

			CLabel preview = new CLabel(this, SWT.BORDER);
			GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
			gridData.widthHint = 25;
			gridData.heightHint = 25;
			preview.setLayoutData(gridData);
			org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
			preview.setBackground(c);
			preview.update();
			colorRegistry.add(c);
			colorLabels.add(preview);

			Label l = new Label(this, SWT.NONE);
			gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
			gridData.widthHint = 220;
			l.setLayoutData(gridData);
			l.setText(category.name);
			classLabels.add(l);
		}
	}

	/**
	 * @param classifier
	 *            setter, see {@link classifier}
	 */
	public void setClassifier(IIDClassifier classifier) {
		this.classifier = classifier;

		for (int i = 0; i < classifier.getDataClasses().size(); i++) {
			SimpleCategory category = classifier.getDataClasses().get(i);
			CLabel colorLabel = colorLabels.get(i);
			org.eclipse.swt.graphics.Color c = category.color.getSWTColor(Display.getCurrent());
			colorLabel.setBackground(c);
			colorLabel.update();

			Label label = classLabels.get(i);
			label.setText(category.name);
		}
	}

	/**
	 * @return the classifier, see {@link #classifier}
	 */
	public IIDClassifier getClassifier() {
		return classifier;
	}

	@Override
	public void dispose() {
		for (org.eclipse.swt.graphics.Color c : colorRegistry) {
			c.dispose();
		}
		colorRegistry.clear();
		super.dispose();
	}

}
