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

import java.io.File;
import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.gui.util.HelpButtonWizardDialog;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Startup procedure for project wizard.
 * 
 * @author Marc Streit
 */
public class GeneticGUIStartupProcedure extends AStartupProcedure {

	private boolean loadSampleData = false;

	// NOTE: change also organism when setting another dataset
	private static String REAL_DATA_SAMPLE_FILE = "data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public GeneticGUIStartupProcedure() {

		// Delete old workbench state
		FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH + ".metadata");
	}

	@Override
	public void init() {

		if (loadSampleData) {

			GeneralManager.get().getBasicInfo().setOrganism(Organism.HOMO_SAPIENS);
		}

		// Start the genetic plugin bundle to trigger mapping loading
		try {
			Bundle bundle = Platform.getBundle("org.caleydo.datadomain.genetic");
			bundle.start();
		} catch (BundleException e) {
			throw new IllegalStateException("Failed to initalize genetic data domain");
		}

		super.init();
	}

	@Override
	public void execute() {
		super.execute();

		DataImportWizard dataImportWizard;

		if (loadSampleData) {
			DataSetDescription dataSetDescription = new DataSetDescription();
			dataSetDescription.setDataSourcePath(REAL_DATA_SAMPLE_FILE.replace("/",
					File.separator));
			dataSetDescription.setMathFilterMode(EDataTransformation.LOG2
					.getHumanReadableRep());
			dataImportWizard = new DataImportWizard(dataSetDescription);
		} else {
			dataImportWizard = new DataImportWizard();
		}

		HelpButtonWizardDialog dialog = new HelpButtonWizardDialog(StartupProcessor.get().getDisplay()
				.getActiveShell(), dataImportWizard);

		if (Window.CANCEL == dialog.open())
			StartupProcessor.get().shutdown();

	}

	public void setLoadSampleData(boolean loadSampleData) {
		this.loadSampleData = loadSampleData;
	}

	@Override
	public void addDefaultStartViews(IFolderLayout layout) {

		layout.addView("org.caleydo.view.dvi");
		layout.addView("org.caleydo.view.stratomex");
	}

	@Override
	public void postWorkbenchOpen() {

		// Make DVI visible if available
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.caleydo.view.dvi");
		} catch (PartInitException e) {
			// do nothing if DVI does not exist
		}
	}
}
