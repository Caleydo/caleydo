/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.ui;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * a swt dialog with a title and description text fields
 *
 * @author Samuel Gratzl
 *
 */
public class TitleDescriptionDialog extends Dialog {
	private final String dialogTitle;
	private String title;
	private String description;
	private Text titleUI;
	private Text descUI;

	public TitleDescriptionDialog(Shell parentShell, String dialogTitle, String initialTitleValue,
			String initialDescriptionValue) {
		super(parentShell);
		this.dialogTitle = dialogTitle;
		this.title = initialTitleValue;
		this.description = initialDescriptionValue;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(dialogTitle);
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		Composite res = (Composite) super.createDialogArea(parent);
		// Composite p = new Composite(res, SWT.NONE)
		Label l = new Label(res, SWT.NONE);
		l.setText("Title:");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		this.titleUI = new Text(res, SWT.BORDER);
		titleUI.setText(title == null ? "" : title);
		titleUI.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		l = new Label(res, SWT.NONE);
		l.setText("Description:");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		this.descUI = new Text(res, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		descUI.setText(description == null ? "" : description);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		descUI.setLayoutData(gd);
		return res;
	}

	/**
	 * @return the title, see {@link #titleUI}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the desc, see {@link #descUI}
	 */
	public String getDescription() {
		return description;
	}

	@Override
	protected void okPressed() {
		title = titleUI.getText();
		description = descUI.getText();
		super.okPressed();
	}
}

