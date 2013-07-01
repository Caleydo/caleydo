/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark.toolbar;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.io.gui.ExportDataDialog;
import org.eclipse.swt.widgets.Shell;

public class ExportDataAction extends SimpleAction {
	private static final String LABEL = "Export data";
	private static final String ICON = "resources/icons/general/export_data.png";

	public ExportDataAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		ExportDataDialog dialog = new ExportDataDialog(new Shell());
		dialog.open();
	}
}
