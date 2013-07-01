/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Input dialog for naming table perspectives. The dialog provides the option that the default name will be used in the
 * future.
 * 
 * @author Marc Streit
 */
public class TablePerspectiveNameInputDialog
	extends InputDialog {

	Button alwaysUseDefaultNameButton;

	public TablePerspectiveNameInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Control control = super.createDialogArea(parent);

		alwaysUseDefaultNameButton = new Button(parent, SWT.CHECK);
		alwaysUseDefaultNameButton
				.setText("Do not ask again and generate default name from now on.");

		return control;
	}

	@Override
	protected void okPressed() {
		MyPreferences.setIsAwaysUseTablePerspectiveDefaultName(alwaysUseDefaultNameButton.getSelection());
		super.okPressed();
	}
}
