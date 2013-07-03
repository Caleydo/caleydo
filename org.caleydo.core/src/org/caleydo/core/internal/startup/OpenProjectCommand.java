/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.startup;

import org.caleydo.core.internal.MyPreferences;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
/**
 * simple restart command to restart the whole application
 * @author Samuel Gratzl
 *
 */
public class OpenProjectCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fileDialog = new FileDialog(HandlerUtil.getActiveShell(event), SWT.OPEN);
		fileDialog.setText("Load Project");
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String fileName = fileDialog.open();
		if (fileName != null) {
			MyPreferences.setAutoLoadProject(fileName);

			// restart
			PlatformUI.getWorkbench().restart();
		}
		return null;
	}
}
