package org.caleydo.rcp;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.AutoSaver;
import org.caleydo.core.serialize.ProjectSaver;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor
	extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "org.caleydo.rcp.perspective";

	private AutoSaver autoSaver;

	private IWorkbenchWindowConfigurer workbenchConfigurer;
	
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		PlatformUI.getPreferenceStore()
			.setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
		workbenchConfigurer = configurer;
		
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}
	
	@Override
	public void postStartup() {
		super.postStartup();

		if (!Application.LAZY_VIEW_LOADING) {
			ASerializedView[] serViews = new ASerializedView[Application.initializedStartViews.size()];
			serViews = Application.initializedStartViews.toArray(serViews);
			IWorkbenchPage activePage = workbenchConfigurer.getWindow().getActivePage();
			for (ASerializedView startView : serViews) {
				try {
					activePage.showView(startView.getViewGUIID());
				} catch (PartInitException ex) {
					ex.printStackTrace();
				}
			}
		}

		// Check if an early exit should be performed
		if (Application.bDoExit) {
			this.getWorkbenchConfigurer().getWorkbench().close();
			return;
		}

		filterPreferencePages();
		initializeViews();

		IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		autoSaver = new AutoSaver();
		vm.getDisplayLoopExecution().executeMultiple(autoSaver);
	}

	private void filterPreferencePages() {
		PreferenceManager preferenceManager =
			this.getWorkbenchConfigurer().getWorkbench().getPreferenceManager();
		preferenceManager.remove("org.eclipse.ui.preferencePages.Workbench");
		preferenceManager.remove("org.eclipse.update.internal.ui.preferences.MainPreferencePage");
		preferenceManager.remove("org.eclipse.help.ui.browsersPreferencePage");
	}
	
	/**
	 * Sets the views init-parameters. In case of a loaded project, the views are
	 * initialized from their restored serialized-representation.
	 */
	private void initializeViews() {
		if (Application.applicationMode == EApplicationMode.LOAD_PROJECT) {
			
		}
	}
	
	@Override
	public boolean preShutdown() {
		super.preShutdown();
		
		IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		vm.getDisplayLoopExecution().stopMultipleExecution(autoSaver);
		autoSaver = null;

		ProjectSaver saver = new ProjectSaver();
		saver.saveRecentProject();

		return true;
	}
}
