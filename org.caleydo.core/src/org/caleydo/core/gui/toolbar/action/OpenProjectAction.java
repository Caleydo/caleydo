/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.internal.MyPreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Button to open a project
 *
 * @author Christian Partl
 *
 */
public class OpenProjectAction extends SimpleAction {
	public static final String LABEL = "Open Project";
	public static final String ICON = "resources/icons/general/open.png";

	/**
	 * Constructor.
	 */
	public OpenProjectAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		FileDialog fileDialog = new FileDialog(new Shell(), SWT.OPEN);
		fileDialog.setText("Load Project");
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String fileName = fileDialog.open();
		if (fileName != null) {
			MyPreferences.setAutoLoadProject(fileName);

			// restart
			PlatformUI.getWorkbench().restart();
		}
	}
}
