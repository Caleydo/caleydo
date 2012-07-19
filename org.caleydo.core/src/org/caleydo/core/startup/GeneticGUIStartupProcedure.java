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
import java.util.List;

import org.caleydo.core.io.gui.DataImportWizard;
import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
	public void init(ApplicationInitData appInitData) {

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

		super.init(appInitData);
	}

	@Override
	public void execute() {
		super.execute();

		if (loadSampleData) {
			ImportDataDialog dialog = new ImportDataDialog(StartupProcessor.get()
					.getDisplay().getActiveShell(), REAL_DATA_SAMPLE_FILE.replace("/",
					File.separator));
			if (Window.CANCEL == dialog.open())
				StartupProcessor.get().shutdown();
		} else {
			// dialog = new ImportDataDialog(StartupProcessor.get().getDisplay()
			// .getActiveShell());

			DataImportWizard dataImportWizard = new DataImportWizard();

			WizardDialog dialog = new WizardDialog(StartupProcessor.get().getDisplay()
					.getActiveShell(), dataImportWizard);

			if (Window.CANCEL == dialog.open())
				StartupProcessor.get().shutdown();
		}

	}

	public void setLoadSampleData(boolean loadSampleData) {
		this.loadSampleData = loadSampleData;
	}

	@Override
	public void addDefaultStartViews() {

		List<Pair<String, String>> startViewWithDataDomain = appInitData
				.getAppArgumentStartViewWithDataDomain();

		// Do not add any default views if at least one view is specified as
		// application argument
		if (!startViewWithDataDomain.isEmpty())
			return;

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.dvi",
				"org.caleydo.datadomain.genetic"));
	}
}
