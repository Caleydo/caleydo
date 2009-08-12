package org.caleydo.rcp.wizard.project;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.manager.usecase.UnspecifiedUseCase;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.wizard.project.ChooseProjectTypePage.EProjectType;
import org.eclipse.jface.wizard.IWizardPage;
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
			ChooseProjectTypePage page =
				(ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME);

			IUseCase useCase;
			
			if (page.getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {

				useCase = new GeneticUseCase();
				
//				if (page.getProjectType() == EProjectType.PATHWAY_VIEWER_MODE) {
//					Application.applicationMode = EApplicationMode.PATHWAY_VIEWER;
//				}
				if (page.getProjectType() == EProjectType.SAMPLE_DATA_RANDOM) {
					Application.applicationMode = EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA_RANDOM;
				}
				else if (page.getProjectType() == EProjectType.SAMPLE_DATA_REAL) {
					Application.applicationMode = EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA_REAL;
				}
				else if (page.getProjectType() == EProjectType.NEW_PROJECT) {
					Application.applicationMode = EApplicationMode.GENE_EXPRESSION_NEW_DATA;
				}
//				else
//					throw new IllegalStateException("Not implemented!");
			} else if (page.getUseCaseMode() == EUseCaseMode.UNSPECIFIED_DATA) {
				useCase = new UnspecifiedUseCase();
				Application.applicationMode = EApplicationMode.UNSPECIFIED_NEW_DATA;
			} else if (page.getUseCaseMode() == EUseCaseMode.LOAD_PROJECT) {
				System.out.println("Load Project");
				ProjectLoader loader = new ProjectLoader();
				if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.RECENT) {
					Application.initData = loader.loadRecent();
				} else if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.SPECIFIED) {
					Application.initData = loader.load(page.getProjectFileName());
				} else {
					throw new IllegalArgumentException("encoutnered unknown project-load-type");
				}
				useCase = Application.initData.getUseCase();
				Application.startViews.clear();
				Application.initializedStartViews = Application.initData.getViews();
				Application.applicationMode = EApplicationMode.LOAD_PROJECT;
				Application.bDeleteRestoredWorkbenchState = true;
			} else if (page.getUseCaseMode() == EUseCaseMode.COLLABORATION_CLIENT) {
				NetworkManager nm = GeneralManager.get().getNetworkManager();
				nm.startNetworkService();
				nm.setNetworkName(page.getNetworkName());
				Application.initData = nm.createConnection(page.getNetworkAddress());
				useCase = Application.initData.getUseCase();
				Application.applicationMode = EApplicationMode.COLLABORATION_CLIENT;
			} else { 
				throw new IllegalStateException("Not implemented!");
			}

			GeneralManager.get().setUseCase(useCase);
			
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

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof ChooseProjectTypePage) {
			if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME)).getProjectType() == EProjectType.NEW_PROJECT) {
				NewProjectImportDataPage nextPage =
					(NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME);

				nextPage.setPageComplete(true);
				return nextPage;
			}
/*			else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME))
				.getProjectType() == EProjectType.EXISTING_PROJECT) {
//				FileOpenProjectAction fileOpenProjectAction = new FileOpenProjectAction(this.getShell());
//				fileOpenProjectAction.run();

				this.performFinish();
			}
*/			else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME))
				.getProjectType() == EProjectType.SAMPLE_DATA_RANDOM) {

			}
			else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME))
				.getProjectType() == EProjectType.SAMPLE_DATA_REAL) {

			}
//			else if (((ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME))
//				.getProjectType() == EProjectType.PATHWAY_VIEWER_MODE) {
//				// Remove heatmap and par coord views
//				for (AGLEventListener glEventListener : GeneralManager.get().getViewGLCanvasManager()
//					.getAllGLEventListeners()) {
//					if (glEventListener instanceof GLHeatMap
//						|| glEventListener instanceof GLParallelCoordinates) {
//						GeneralManager.get().getViewGLCanvasManager().unregisterGLEventListener(
//							glEventListener);
//					}
//				}
//
//				this.performFinish();
//			}
		}

		return page;
	}
}