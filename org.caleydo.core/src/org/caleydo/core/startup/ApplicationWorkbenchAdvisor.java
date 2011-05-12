package org.caleydo.core.startup;

import org.caleydo.core.serialize.AutoSaver;
import org.caleydo.core.serialize.ProjectSaver;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor
	extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "org.caleydo.core.gui.perspective";

	private AutoSaver autoSaver;

	// private IWorkbenchWindowConfigurer workbenchConfigurer;

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		PlatformUI.getPreferenceStore()
			.setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
		// workbenchConfigurer = configurer;

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

		// if (!Application.LAZY_VIEW_LOADING) {
		// String[] serViews = new String[Application.initializedStartViews.size()];
		// serViews = Application.initializedStartViews.toArray(serViews);
		// IWorkbenchPage activePage = workbenchConfigurer.getWindow().getActivePage();
		// for (String startView : serViews) {
		// try {
		// activePage.showView(startView);
		// }
		// catch (PartInitException ex) {
		// ex.printStackTrace();
		// }
		// }
		// }

		// // Check if an early exit should be performed
		// if (Application.bDoExit) {
		// this.getWorkbenchConfigurer().getWorkbench().close();
		// return;
		// }

		filterPreferencePages();
		// initializeViews();

		// FIXME: turn on auto saver here when data domain integration is finished
		// autoSaver = new AutoSaver();
		// ViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		// vm.getDisplayLoopExecution().executeMultiple(autoSaver);
	}

	private void filterPreferencePages() {
		PreferenceManager preferenceManager =
			this.getWorkbenchConfigurer().getWorkbench().getPreferenceManager();

		for (Object node : preferenceManager.getElements(PreferenceManager.PRE_ORDER)) {

			IPreferenceNode prefNode = (IPreferenceNode) node;
			if (!prefNode.getId().contains("org.caleydo.core")) {
				preferenceManager.remove(prefNode);
			}
		}
	}

	// /**
	// * Sets the views init-parameters. In case of a loaded project, the views are initialized from their
	// * restored serialized-representation.
	// */
	// private void initializeViews() {
	// // if (Application.applicationMode == ApplicationMode.LOAD_PROJECT) {
	// //
	// // }
	// }

	@Override
	public boolean preShutdown() {
		super.preShutdown();

//		ViewManager vm = GeneralManager.get().getViewGLCanvasManager();
//		vm.getDisplayLoopExecution().stopMultipleExecution(autoSaver);
		autoSaver = null;

		ProjectSaver saver = new ProjectSaver();
		saver.saveRecentProject();

		return true;
	}
}
