/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.datadomain.image.wizard.ImageImportWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

/**
 * @author Thomas Geymayer
 *
 */
public class ImportImageHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImageImportWizard imageImportWizard = new ImageImportWizard();
		new HelpButtonWizardDialog(Display.getDefault().getActiveShell(), imageImportWizard).open();
		return null;
	}

}
