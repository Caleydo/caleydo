/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.serialize.ProjectManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SaveProjectAction extends SimpleAction {

	public static final String LABEL = "Save Project";
	public static final String ICON = "resources/icons/general/save.png";

	/**
	 * Constructor.
	 */
	public SaveProjectAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
		fileDialog.setText("Save Project");
		String[] filterExt = { "*.cal" };
		fileDialog.setFilterExtensions(filterExt);

		String filePath = "caleydo-project_" + new SimpleDateFormat("yyyy.MM.dd_HH.mm").format(new Date()) + ".cal";

		fileDialog.setFileName(filePath);
		final String fileName = fileDialog.open();

		if (fileName == null)
			return;

		try {
			new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false,
					ProjectManager.save(fileName));
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				.setText("Caleydo - "
				+ fileName.substring(fileName.lastIndexOf("/") + 1));
	}
}
