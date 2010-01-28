package org.caleydo.rcp.wizard.project;

import java.io.IOException;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.UnspecifiedUseCase;
import org.caleydo.core.net.StandardGroupwareManager;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.wizard.project.ChooseProjectTypePage.EProjectType;
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

			// When the user changed the selection use case mode compared to the stored mode in the
			// preferences the old workbench state should be deleted.
			// EApplicationMode eOldUseCaseMode =

			EDataDomain dataDomain =
				EDataDomain.valueOf(prefStore.getString(PreferenceConstants.LAST_CHOSEN_USE_CASE_MODE));
			EApplicationMode eOldUseCaseMode = EApplicationMode.getApplicationModeFromDomain(dataDomain);

			if (page.getApplicationMode() != eOldUseCaseMode)
				Application.bDeleteRestoredWorkbenchState = true;

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_ORGANISM, page.getOrganism().name());

			try {
				prefStore.save();
			}
			catch (IOException e) {
				throw new IllegalStateException("Unable to save preference file.");
			}

			IUseCase useCase;
			if (page.getApplicationMode() == EApplicationMode.GENE_EXPRESSION_NEW_DATA
				|| page.getApplicationMode() == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA) {
				Application.dataDomain = EDataDomain.GENETIC_DATA;
				useCase = new GeneticUseCase();
				((GeneticUseCase) useCase).setOrganism(page.getOrganism());

				// if (page.getProjectType() == EProjectType.PATHWAY_VIEWER_MODE) {
				// Application.applicationMode = EApplicationMode.PATHWAY_VIEWER;
				// }
				if (page.getProjectType() == EProjectType.SAMPLE_DATA_REAL) {
					Application.applicationMode = EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA;
				}
				else if (page.getProjectType() == EProjectType.NEW_PROJECT) {
					Application.applicationMode = EApplicationMode.GENE_EXPRESSION_NEW_DATA;
				}

				String sNewPathwayDataSources = "";
				if (page.isKEGGPathwayDataLoadingRequested())
					sNewPathwayDataSources += EPathwayDatabaseType.KEGG.name() + ";";
				if (page.isBioCartaPathwayLoadingRequested())
					sNewPathwayDataSources += EPathwayDatabaseType.BIOCARTA.name() + ";";

				if (sNewPathwayDataSources != prefStore
					.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES))
					Application.bDeleteRestoredWorkbenchState = true;

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES,
					sNewPathwayDataSources);

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_USE_CASE_MODE, Application.dataDomain
					.name());
			}
			else if (page.getApplicationMode() == EApplicationMode.UNSPECIFIED_NEW_DATA) {
				useCase = new UnspecifiedUseCase();
				Application.dataDomain = EDataDomain.UNSPECIFIED;
				Application.applicationMode = EApplicationMode.UNSPECIFIED_NEW_DATA;

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_USE_CASE_MODE, Application.dataDomain
					.name());
			}
			else if (page.getApplicationMode() == EApplicationMode.LOAD_PROJECT) {
				// FIXME determine the application domain somewhere?
				System.out.println("Load Project");
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
				useCase = Application.initData.getUseCase();
				Application.startViews.clear();
				Application.initializedStartViews = Application.initData.getViewIDs();
				Application.applicationMode = EApplicationMode.LOAD_PROJECT;
				Application.bDeleteRestoredWorkbenchState = true;
			}
			else if (page.getApplicationMode() == EApplicationMode.COLLABORATION_CLIENT) {
				StandardGroupwareManager groupwareManager = new StandardGroupwareManager();
				groupwareManager.setNetworkName(page.getNetworkName());
				groupwareManager.setServerAddress(page.getNetworkAddress());
				groupwareManager.startClient();
				Application.initData = groupwareManager.getInitData();
				useCase = Application.initData.getUseCase();
				Application.applicationMode = EApplicationMode.COLLABORATION_CLIENT;
			}
			else {
				throw new IllegalStateException("Not implemented!");
			}

			GeneralManager.get().addUseCase(useCase);

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

	// @Override
	// public IWizardPage getNextPage(IWizardPage page) {
	// if (page instanceof ChooseProjectTypePage) {
	//
	// ChooseProjectTypePage projectPage = (ChooseProjectTypePage) page;
	//
	//
	// if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).getProjectType() ==
	// EProjectType.NEW_PROJECT) {
	// NewProjectImportDataPage nextPage =
	// (NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME);
	//
	// nextPage.setPageComplete(true);
	// return nextPage;
	// }
	// /*
	// * else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)) .getProjectType()
	// * == EProjectType.EXISTING_PROJECT) { // FileOpenProjectAction fileOpenProjectAction = new
	// * FileOpenProjectAction(this.getShell()); // fileOpenProjectAction.run(); this.performFinish(); }
	// */else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).getProjectType() ==
	// EProjectType.SAMPLE_DATA_RANDOM) {
	//
	// }
	// else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).getProjectType() ==
	// EProjectType.SAMPLE_DATA_REAL) {
	//
	// }
	// // else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME))
	// // .getProjectType() == EProjectType.PATHWAY_VIEWER_MODE) {
	// // // Remove heatmap and par coord views
	// // for (AGLEventListener glEventListener : GeneralManager.get().getViewGLCanvasManager()
	// // .getAllGLEventListeners()) {
	// // if (glEventListener instanceof GLHeatMap
	// // || glEventListener instanceof GLParallelCoordinates) {
	// // GeneralManager.get().getViewGLCanvasManager().unregisterGLEventListener(
	// // glEventListener);
	// // }
	// // }
	// //
	// // this.performFinish();
	// // }
	// }
	//
	// return page;
	// }
}