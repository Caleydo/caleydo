package org.caleydo.rcp.startup;

import java.util.List;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.Organism;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Startup procedure for project wizard.
 * 
 * @author Marc Streit
 *
 */
public class GUIStartupProcedure
	extends AStartupProcedure {

	private boolean loadSampleData = false;

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	@Override
	public void init(ApplicationInitData appInitData) {

		super.init(appInitData);
		
		GeneralManager.get().getXmlParserManager().parseXmlFileByName("data/bootstrap/bootstrap.xml");
		

		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.genetic");
		cmd.doCommand();

		GeneralManager.get().setOrganism(Organism.HOMO_SAPIENS);

		
		if (loadSampleData) {	
			appInitData.setLoadPathways(true);
		}
	}

	@Override
	public void execute() {
		super.execute();

		Shell shell = StartupProcessor.get().getDisplay().getActiveShell();

		WizardDialog dataImportWizard;
		
		if (loadSampleData) {
			
			dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));
			
			if (Window.CANCEL == dataImportWizard.open()) {
				StartupProcessor.get().shutdown();
			}
		}
		else {
			dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				StartupProcessor.get().shutdown();
			}
		}
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
		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.histogram",
			"org.caleydo.datadomain.genetic"));

		if (appInitData.isLoadPathways()) {
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.bucket",
				"org.caleydo.datadomain.genetic"));
		}
	}
}
