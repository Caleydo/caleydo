/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.util.svg;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class ExportSVGHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbench w = PlatformUI.getWorkbench();
		Display display = w.getDisplay();
		Shell shell = display.getActiveShell();

		IWorkbenchPart active = w.getActiveWorkbenchWindow().getActivePage().getActivePart();
		IView view = null;
		if (active instanceof CaleydoRCPViewPart)
			view = ((CaleydoRCPViewPart) active).getView();

		if (view == null)
			return null;

		String path = getFilePath(shell);
		if (path == null)
			return null;

		File f = new File(path);

		SVGGraphicsTracerFactory.enable(view, f);
		return null;
	}

	private static String getFilePath(Shell shell) {
		String path = "screenshot_" + getDateTime() + ".svg";

		FileDialog saveFileDialog = new FileDialog(shell, SWT.SAVE);
		saveFileDialog.setFilterExtensions(new String[] { "*.svg" });
		saveFileDialog.setFileName(path);
		path = saveFileDialog.open();
		// check if file dialog was canceled
		if (path == null)
			return null;

		if (!path.endsWith(".svg"))
			path += ".svg";

		return path;
	}

	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
