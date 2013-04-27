/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Startup procedure for project wizard.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GenericGUIStartupProcedure
	extends AStartupProcedure {

	public GenericGUIStartupProcedure() {
		// Delete old workbench state
		FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH + ".metadata");
	}

	@Override
	public void init() {

		DataDomainManager.get().initalizeDataDomain(
				"org.caleydo.datadomain.generic");
	}

	@Override
	public void execute() {
		super.execute();

		DataImportWizard dataImportWizard = new DataImportWizard();

		HelpButtonWizardDialog dialog = new HelpButtonWizardDialog(StartupProcessor.get()
				.getDisplay().getActiveShell(), dataImportWizard);

		if (Window.CANCEL == dialog.open())
			StartupProcessor.get().shutdown();
	}

	@Override
	public void postWorkbenchOpen() {

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
