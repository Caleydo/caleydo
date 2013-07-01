/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget;

import org.caleydo.core.util.base.BooleanCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectAllNoneWidget {
	private final Group group;

	public SelectAllNoneWidget(Composite parent, final BooleanCallback callback) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		this.group.setText("Column Selection");
		this.group.setLayout(new GridLayout(2, false));
		this.group.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		Button all = new Button(this.group, SWT.PUSH);
		all.setText("Select All");
		all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(true);
			}
		});
		Button none = new Button(this.group, SWT.PUSH);
		none.setText("Select None");
		none.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(false);
			}
		});
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
	}
}
