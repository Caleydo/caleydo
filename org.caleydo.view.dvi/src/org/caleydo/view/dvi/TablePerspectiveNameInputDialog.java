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
package org.caleydo.view.dvi;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Input dialog for naming table perspectives. The dialog provides the option
 * that the default name will be used in the future.
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

		GeneralManager
				.get()
				.getPreferenceStore()
				.setValue(PreferenceConstants.DVI_ALWAYS_USE_TABLE_PERSPECTIVE_DEFAULT_NAME,
						alwaysUseDefaultNameButton.getSelection());

		super.okPressed();
	}
}
