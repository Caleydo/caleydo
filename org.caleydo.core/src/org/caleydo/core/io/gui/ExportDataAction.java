/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui;

import org.caleydo.core.gui.SimpleAction;
import org.eclipse.swt.widgets.Shell;

public class ExportDataAction extends SimpleAction {
	public static final String LABEL = "Export Data";
	public static final String ICON = "resources/icons/general/export_data.png";

	public ExportDataAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		new ExportDataDialog(new Shell()).open();
	}
}
