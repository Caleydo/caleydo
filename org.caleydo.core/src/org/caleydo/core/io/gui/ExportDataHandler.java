/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public class ExportDataHandler
	extends AbstractHandler
	implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExportDataDialog dialog = new ExportDataDialog(new Shell());
		dialog.open();

		return null;
	}
}
