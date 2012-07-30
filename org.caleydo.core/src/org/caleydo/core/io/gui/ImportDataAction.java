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
package org.caleydo.core.io.gui;

import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.startup.StartupProcessor;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ImportDataAction extends AToolBarAction {
	public static final String TEXT = "Load data";
	public static final String ICON = "resources/icons/general/load_data.png";

	/**
	 * Constructor.
	 */
	public ImportDataAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		// new ImportDataDialog(new Shell()).open();

		DataImportWizard dataImportWizard = new DataImportWizard();

		new HelpButtonWizardDialog(StartupProcessor.get().getDisplay().getActiveShell(),
				dataImportWizard).open();
	}
}
