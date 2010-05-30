package org.caleydo.rcp.wizard.project;

import java.io.IOException;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.net.StandardGroupwareManager;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears after Caleydo startup.
 * 
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public class CaleydoProjectWizard
	extends Wizard {

	public static final String SAMPLE_PROJECT_LOCATION = "data/sample_project/sample_project.cal";

	/**
	 * Constructor.
	 */
	public CaleydoProjectWizard(final Shell parentShell) {

		this.setWindowTitle("Caleydo - Project Wizard");

		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
		parentShell.setActive();
	}

	@Override
	public void addPages() {
		addPage(new ChooseProjectTypePage());
	}

	@Override
	public boolean performFinish() {
		if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).isPageComplete()) {
			ChooseProjectTypePage page = (ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME);

			PreferenceStore prefStore = GeneralManager.get().getPreferenceStore();

			EApplicationMode previousApplicationMode =
				EApplicationMode.valueOf(prefStore
					.getString(PreferenceConstants.LAST_CHOSEN_APPLICATION_MODE));

			if (page.getApplicationMode() != previousApplicationMode)
				Application.bDeleteRestoredWorkbenchState = true;

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_ORGANISM, page.getOrganism().name());

			EApplicationMode appMode = page.getApplicationMode();
			if (appMode == EApplicationMode.SAMPLE_PROJECT) {

				GeneralManager.get().getLogger().log(
					new Status(IStatus.INFO, Activator.PLUGIN_ID, "Load sample project"));

				ProjectLoader loader = new ProjectLoader();

				Application.initData = loader.load(SAMPLE_PROJECT_LOCATION);

				//dataDomain = Application.initData.getDataDomain();
				Application.startViewWithDataDomain.clear();
				Application.initializedStartViews = Application.initData.getViewIDs();
				Application.applicationMode = EApplicationMode.SAMPLE_PROJECT;
				Application.bDeleteRestoredWorkbenchState = true;

			}
			else if (appMode == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA) {

				CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
				cmd.setAttributes("org.caleydo.datadomain.genetic");
				cmd.doCommand();
				//dataDomain = cmd.getCreatedObject();

				GeneralManager.get().setOrganism(EOrganism.HOMO_SAPIENS);

				Application.applicationMode = appMode;

				String sNewPathwayDataSources =
					EPathwayDatabaseType.KEGG.name() + ";" + EPathwayDatabaseType.BIOCARTA.name() + ";";

				if (sNewPathwayDataSources != prefStore
					.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES))
					Application.bDeleteRestoredWorkbenchState = true;

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES,
					sNewPathwayDataSources);

				cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
				cmd.setAttributes("org.caleydo.datadomain.pathway");
				cmd.doCommand();
			}
			else if (appMode == EApplicationMode.GENE_EXPRESSION_NEW_DATA) {
				CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
				cmd.setAttributes("org.caleydo.datadomain.genetic");
				cmd.doCommand();
				//dataDomain = cmd.getCreatedObject();

				GeneralManager.get().setOrganism(page.getOrganism());

				Application.applicationMode = appMode;

				boolean loadPathways = false;

				String sNewPathwayDataSources = "";
				if (page.isKEGGPathwayDataLoadingRequested()) {
					loadPathways = true;
					sNewPathwayDataSources += EPathwayDatabaseType.KEGG.name() + ";";
				}
				if (page.isBioCartaPathwayLoadingRequested()) {
					loadPathways = true;
					sNewPathwayDataSources += EPathwayDatabaseType.BIOCARTA.name() + ";";
				}

				if (loadPathways) {
					cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
					cmd.setAttributes("org.caleydo.datadomain.pathway");
					cmd.doCommand();
				}

				if (sNewPathwayDataSources != prefStore
					.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES))
					Application.bDeleteRestoredWorkbenchState = true;

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES,
					sNewPathwayDataSources);

			}
			else if (appMode == EApplicationMode.UNSPECIFIED_NEW_DATA) {
				CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
				cmd.setAttributes("org.caleydo.datadomain.generic");
				cmd.doCommand();
				Application.applicationMode = EApplicationMode.UNSPECIFIED_NEW_DATA;

			}
			else if (appMode == EApplicationMode.LOAD_PROJECT) {

				GeneralManager.get().getLogger().log(
					new Status(IStatus.INFO, Activator.PLUGIN_ID, "Load existing project."));

				ProjectLoader loader = new ProjectLoader();
				if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.RECENT) {
					Application.initData = loader.loadRecent();
				}
				else if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.SPECIFIED) {
					Application.initData = loader.load(page.getProjectFileName());
				}
				else {
					throw new IllegalArgumentException("encoutnered unknown project-load-type");
				}
				//dataDomain = Application.initData.getDataDomain();
				Application.startViewWithDataDomain.clear();
				Application.initializedStartViews = Application.initData.getViewIDs();
				Application.applicationMode = EApplicationMode.LOAD_PROJECT;
				Application.bDeleteRestoredWorkbenchState = true;
			}
			else if (appMode == EApplicationMode.COLLABORATION_CLIENT) {
				StandardGroupwareManager groupwareManager = new StandardGroupwareManager();
				groupwareManager.setNetworkName(page.getNetworkName());
				groupwareManager.setServerAddress(page.getNetworkAddress());
				groupwareManager.startClient();
				Application.initData = groupwareManager.getInitData();
				//dataDomain = Application.initData.getDataDomain();
				Application.applicationMode = EApplicationMode.COLLABORATION_CLIENT;
			}
			else {
				throw new IllegalStateException("Not implemented!");
			}

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_APPLICATION_MODE, Application.applicationMode
				.name());

			try {
				prefStore.save();
			}
			catch (IOException e) {
				throw new IllegalStateException("Unable to save preference file.");
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean performCancel() {

		Application.bDoExit = true;
		return true;
	}

	@Override
	public boolean canFinish() {
		if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).isPageComplete())
			return true;

		return false;
	}

	/** Main method for testing */
	public static void main(String args[]) {
		while (true) {
			Shell shell = new Shell();
			WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));
			projectWizardDialog.open();
		}
	}
}