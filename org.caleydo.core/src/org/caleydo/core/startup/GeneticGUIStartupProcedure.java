package org.caleydo.core.startup;

import java.util.List;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.specialized.Organism;
import org.caleydo.core.serialize.ProjectSaver;
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

	@Override
	public void init(ApplicationInitData appInitData) {

		if (loadSampleData) {
			appInitData.setLoadPathways(true);

			GeneralManager.get().getPreferenceStore()
				.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES, "KEGG;BioCarta");

			GeneralManager.get().getBasicInfo().setOrganism(Organism.HOMO_SAPIENS);

			// Delete old workbench state
			FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH + ".metadata");
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

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser",
			"org.caleydo.datadomain.genetic"));
		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.heatmap.hierarchical",
			"org.caleydo.datadomain.genetic"));
		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords",
			"org.caleydo.datadomain.genetic"));

		if (appInitData.isLoadPathways()) {
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.bucket",
				"org.caleydo.datadomain.genetic"));
		}
	}
}
