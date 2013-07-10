/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.serialize.ProjectManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * Startup procedure for project wizard.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ImportStartupProcedure implements IStartupProcedure {

	public ImportStartupProcedure() {
		// Delete old workbench state
		ProjectManager.deleteWorkbenchSettings();
	}

	@Override
	public boolean preWorkbenchOpen() {
		return true;
	}

	@Override
	public void run() {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				DataImportWizard dataImportWizard = createDataImportWizard();
				HelpButtonWizardDialog dialog = new HelpButtonWizardDialog(Display.getCurrent().getActiveShell(),
						dataImportWizard);

				dialog.open();
			}
		});

		return;
	}

	protected DataImportWizard createDataImportWizard() {
		return new DataImportWizard();
	}

	@Override
	public void postWorkbenchOpen(IWorkbenchWindowConfigurer configurer) {
		// Make DVI visible if available
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.caleydo.view.dvi");
		}
		catch (PartInitException e) {
			// do nothing if DVI does not exist
		}
	}
}
