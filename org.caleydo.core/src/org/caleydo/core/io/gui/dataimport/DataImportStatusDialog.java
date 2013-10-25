/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.util.AStatusDialog;
import org.caleydo.core.gui.util.FontUtil;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class DataImportStatusDialog extends AStatusDialog {

	private String fileName;

	private List<Pair<String, String>> attributes = new ArrayList<>();

	/**
	 * @param parentShell
	 */
	public DataImportStatusDialog(Shell parentShell, String title, String fileName) {
		super(parentShell, title);
		this.fileName = fileName;
	}

	public void addAttribute(String attribute, String value) {
		attributes.add(Pair.make(attribute, value));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));
		parentComposite.setLayoutData(new GridData(400, 200));
		Label statusLabel = new Label(parentComposite, SWT.NONE | SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd.widthHint = 400;
		statusLabel.setLayoutData(gd);
		statusLabel.setText("The file " + fileName + " was imported successfully!");
		for (Pair<String, String> attribute : attributes) {
			Label attributeLabel = new Label(parentComposite, SWT.NONE | SWT.WRAP);
			gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			gd.widthHint = 320;
			attributeLabel.setLayoutData(gd);
			attributeLabel.setText(attribute.getFirst());
			FontUtil.makeBold(attributeLabel);

			Label valueLabel = new Label(parentComposite, SWT.RIGHT);
			valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
			valueLabel.setText(attribute.getSecond());
		}
		return super.createDialogArea(parent);
	}
}
