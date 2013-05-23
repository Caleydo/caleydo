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
package org.caleydo.core.io.gui.dataimport;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for crating a new category.
 *
 * @author Christian Partl
 *
 */
public class CreateCategoryDialog extends Dialog {

	protected Text valueText;
	protected Text nameText;

	protected String value;
	protected String name;

	protected Set<String> existingCategoryValues;

	private class InputValidator implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			if (valueText.getText().equals("") || nameText.getText().equals("")) {
				getButton(OK).setEnabled(false);
			} else {
				getButton(OK).setEnabled(true);
			}
		}
	}

	/**
	 * @param parent
	 * @param
	 */
	public CreateCategoryDialog(Shell parent, Set<String> existingCategoryValues) {
		super(parent);
		this.existingCategoryValues = existingCategoryValues;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create Category");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));

		InputValidator inputValidator = new InputValidator();

		Label valueLabel = new Label(parentComposite, SWT.NONE);
		valueLabel.setText("Value");
		valueText = new Text(parentComposite, SWT.BORDER);
		valueText.addModifyListener(inputValidator);

		Label nameLabel = new Label(parentComposite, SWT.NONE);
		nameLabel.setText("Name");
		nameText = new Text(parentComposite, SWT.BORDER);
		nameText.addModifyListener(inputValidator);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(OK).setEnabled(false);
	}

	@Override
	protected void okPressed() {
		value = valueText.getText();
		name = nameText.getText();
		if (existingCategoryValues.contains(value)) {
			MessageDialog.openError(new Shell(), "Category Value Already Exists",
					"A category that refers to the specified value already exists. Please choose a different value.");
			return;
		}
		super.okPressed();
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
