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
package org.caleydo.vis.rank.internal.ui;


import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class TitleDescriptionDialog extends TitleAreaDialog {
	private final String dialogTitle;
	private final String dialogMessage;
	private String title;
	private String description;
	private Text titleUI;
	private Text descUI;

	public TitleDescriptionDialog(Shell parentShell, String dialogTitle, String dialogMessage,
			String initialTitleValue, String initialDescriptionValue) {
		super(parentShell);
		this.dialogTitle = dialogTitle;
		this.dialogMessage = dialogMessage;
		this.title = initialTitleValue;
		this.description = initialDescriptionValue;
	}

	@Override
	public void create() {
		super.create();
		setTitle(dialogTitle);
		setMessage(dialogMessage);
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

