/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget;

import org.caleydo.core.util.base.ICallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class LoadFileWidget {
	private final ICallback<String> callback;
	private final Text label;

	public LoadFileWidget(Composite parent, String label, ICallback<String> callback, Object layoutData) {
		this.callback = callback;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Input File");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(layoutData);

		Button openFileButton = new Button(group, SWT.PUSH);
		openFileButton.setText(label);
		openFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				onOpenFile();
			}
		});
		this.label = new Text(group, SWT.BORDER);
		this.label.setEnabled(false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = 200;
		this.label.setLayoutData(gridData);
	}


	protected void onOpenFile() {
		FileDialog fileDialog = new FileDialog(new Shell());
		fileDialog.setText("Open");
		// fileDialog.setFilterPath(filePath);
		String[] filterExt = { "*.csv;*.txt;*.gct;*.tsv", "*.*" };
		fileDialog.setFilterExtensions(filterExt);

		String inputFileName = fileDialog.open();
		if (inputFileName == null)
			return;
		inputFileName = inputFileName.trim();
		label.setText(inputFileName);
		callback.on(inputFileName);
	}


	public String getFileName() {
		return this.label.getText();
	}

	public void setFileName(String text) {
		this.label.setText(text);
	}

}
