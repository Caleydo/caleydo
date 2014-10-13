/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui;

import org.caleydo.core.io.gui.mapping.ImportIDMappingDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public class ImportMappingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ImportIDMappingDialog dialog = new ImportIDMappingDialog(Display.getDefault().getActiveShell());
		dialog.open();

		return null;
	}

}
