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

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.jface.window.Window;

/**
 * Startup procedure for project wizard.
 * 
 * @author Marc Streit
 */
public class GeneticGUIStartupProcedure
	extends AStartupProcedure {

	private boolean loadSampleData = false;

	// NOTE: change also organism when setting another dataset
	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	// "data/genome/microarray/kashofer/mouse/all_mice_plus_SN_only_with_mapping.csv";

	public GeneticGUIStartupProcedure() {

		// Delete old workbench state
		FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH + ".metadata");
	}

	@Override
	public void init(ApplicationInitData appInitData) {

		if (loadSampleData) {
			appInitData.setLoadPathways(true);

			GeneralManager.get().getPreferenceStore()
				.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES, "KEGG;BioCarta");

			GeneralManager.get().getBasicInfo().setOrganism(Organism.HOMO_SAPIENS);
		}

		this.dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get()
				.createDataDomain("org.caleydo.datadomain.genetic");

		super.init(appInitData);
	}

	@Override
	public void execute() {
		super.execute();

		ImportDataDialog dialog;

		if (loadSampleData)
			dialog =
				new ImportDataDialog(StartupProcessor.get().getDisplay().getActiveShell(),
					REAL_DATA_SAMPLE_FILE, dataDomain);
		else
			dialog = new ImportDataDialog(StartupProcessor.get().getDisplay().getActiveShell(), dataDomain);

		if (Window.CANCEL == dialog.open())
			StartupProcessor.get().shutdown();

	}

	public void setLoadSampleData(boolean loadSampleData) {
		this.loadSampleData = loadSampleData;
	}

	@Override
	public void addDefaultStartViews() {

		List<Pair<String, String>> startViewWithDataDomain =
			appInitData.getAppArgumentStartViewWithDataDomain();

		// Do not add any default views if at least one view is specified as application argument
		if (!startViewWithDataDomain.isEmpty())
			return;

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.heatmap.hierarchical",
			"org.caleydo.datadomain.genetic"));
		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords",
			"org.caleydo.datadomain.genetic"));
		// startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser",
		// "org.caleydo.datadomain.genetic"));

		if (appInitData.isLoadPathways()) {
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.bucket",
				"org.caleydo.datadomain.genetic"));
		}
	}
}
