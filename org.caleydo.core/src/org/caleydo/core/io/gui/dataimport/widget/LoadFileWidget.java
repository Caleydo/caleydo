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

	public LoadFileWidget(Composite parent, String label, ICallback<String> callback) {
		this.callback = callback;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Input File");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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
		String[] filterExt = { "*.csv;*.txt;*.gct", "*.*" };
		fileDialog.setFilterExtensions(filterExt);

		String inputFileName = fileDialog.open().trim();

		if (inputFileName == null)
			return;
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
