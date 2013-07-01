/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class LabelWidget {
	private final Text label;

	public LabelWidget(Composite parent, String label) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText(label);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		this.label = new Text(group, SWT.BORDER);
		this.label.setEnabled(false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = 100;
		this.label.setLayoutData(gridData);
	}


	public String getText() {
		return this.label.getText();
	}

	public void setText(String text) {
		this.label.setText(text);
	}

	public void setEnabled(boolean enabled) {
		this.label.setEnabled(enabled);
	}
}
