package org.caleydo.core.startup.gui;

import java.io.IOException;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.net.StandardGroupwareManager;
import org.caleydo.core.startup.ApplicationMode;
import org.caleydo.core.startup.GeneticGUIStartupProcedure;
import org.caleydo.core.startup.SerializationStartupProcedure;
import org.caleydo.core.startup.StartupProcessor;
import org.caleydo.core.startup.gui.ChooseProjectTypePage.EProjectLoadType;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.Wizard;
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

			// ProjectMode previousProjectMode =
			// ProjectMode.valueOf(prefStore.getString(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE));

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_ORGANISM, page.getOrganism().name());

			ProjectMode projectMode = page.getProjectMode();
			if (projectMode == ProjectMode.LOAD_PROJECT) {

				SerializationStartupProcedure startupProcedure =
					(SerializationStartupProcedure) StartupProcessor.get().createStartupProcedure(
						ApplicationMode.SERIALIZATION);
				if (page.getProjectLoadType().equals(EProjectLoadType.RECENT)) {
					startupProcedure.setLoadRecentProject(true);
				}
				else {
					startupProcedure.setLoadRecentProject(false);
					startupProcedure.setProjectLocation(page.getProjectFileName());
				}
			}
			else if (projectMode == ProjectMode.SAMPLE_PROJECT) {
				SerializationStartupProcedure startupProcedure =
					(SerializationStartupProcedure) StartupProcessor.get().createStartupProcedure(
						ApplicationMode.SERIALIZATION);
				startupProcedure.loadSampleProject(true);

			}
			else if (projectMode == ProjectMode.GENE_EXPRESSION_SAMPLE_DATA) {

				GeneticGUIStartupProcedure startupProcedure =
					(GeneticGUIStartupProcedure) StartupProcessor.get().createStartupProcedure(
						ApplicationMode.GUI);
				startupProcedure.setLoadSampleData(true);
			}
			else if (projectMode == ProjectMode.GENE_EXPRESSION_NEW_DATA) {

				boolean loadPathways = false;

				String sNewPathwayDataSources = "";
				if (page.isKEGGPathwayDataLoadingRequested()) {
					loadPathways = true;
					sNewPathwayDataSources += "KEGG" + ";";
				}
				if (page.isBioCartaPathwayLoadingRequested()) {
					loadPathways = true;
					sNewPathwayDataSources += "BioCarta" + ";";
				}

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES,
					sNewPathwayDataSources);

				StartupProcessor.get().createStartupProcedure(ApplicationMode.GUI);
				StartupProcessor.get().getAppInitData().setLoadPathways(loadPathways);

				GeneralManager.get().getBasicInfo().setOrganism(page.getOrganism());
			}
			else if (projectMode == ProjectMode.UNSPECIFIED_NEW_DATA) {
				StartupProcessor.get().createStartupProcedure(ApplicationMode.GENERIC);

			}
			else if (projectMode == ProjectMode.COLLABORATION_CLIENT) {
				StandardGroupwareManager groupwareManager = new StandardGroupwareManager();
				groupwareManager.setNetworkName(page.getNetworkName());
				groupwareManager.setServerAddress(page.getNetworkAddress());
				groupwareManager.startClient();

				// TODO create groupwarestartupprocedure
				// groupwareManager.getInitData();
			}
			else {
				throw new IllegalStateException("Not implemented!");
			}

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE, projectMode.name());

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

		// Application.bDoExit = true;
		return true;
	}

	@Override
	public boolean canFinish() {
		if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).isPageComplete())
			return true;

		return false;
	}
}