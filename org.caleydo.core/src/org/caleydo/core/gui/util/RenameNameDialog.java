/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.util;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Alexander Lex
 */
public class RenameNameDialog {
	public static String show(Shell shell, String title, String original) {
		IInputValidator validator = new IInputValidator() {
			@Override
			public String isValid(String newText) {
				if (newText == null || newText.trim().length() == 0)
					return "Please enter a name.";
				else
					return null;
			}
		};

		InputDialog dialog = new InputDialog(new Shell(), title, title, original, validator);
		if (dialog.open() == Window.OK) {
			return dialog.getValue();
		} else
			return null;
	}
}
