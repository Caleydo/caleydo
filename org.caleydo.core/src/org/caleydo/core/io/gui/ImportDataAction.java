/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.eclipse.swt.widgets.Display;

public class ImportDataAction extends SimpleAction {

	public static final String LABEL = "Load data";
	public static final String ICON = "resources/icons/general/load_data.png";

	/**
	 * Constructor.
	 */
	public ImportDataAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		// new ImportDataDialog(new Shell()).open();

		DataImportWizard dataImportWizard = new DataImportWizard();

		new HelpButtonWizardDialog(Display.getDefault().getActiveShell(), dataImportWizard).open();
	}
}
