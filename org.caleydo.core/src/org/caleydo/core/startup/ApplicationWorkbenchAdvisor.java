package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;
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

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		PlatformUI.getPreferenceStore()
			.setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);

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

		filterPreferencePages();

		autoSaver = new AutoSaver();
		GeneralManager.get().getViewManager().getDisplayLoopExecution().executeMultiple(autoSaver);
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

	@Override
	public boolean preShutdown() {
		super.preShutdown();

		GeneralManager.get().getViewManager().getDisplayLoopExecution().stopMultipleExecution(autoSaver);
		autoSaver = null;

		new ProjectSaver().saveRecentProject();

		return true;
	}
}
