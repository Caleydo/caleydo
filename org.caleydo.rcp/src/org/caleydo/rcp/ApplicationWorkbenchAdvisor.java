package org.caleydo.rcp;

import org.caleydo.core.serialize.ProjectSaver;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor
	extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "org.caleydo.rcp.perspective";

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

		// Check if an early exit should be performed
		if (Application.bDoExit) {
			this.getWorkbenchConfigurer().getWorkbench().close();
			return;
		}

		filterPreferencePages();
		initializeViews();
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

		ProjectSaver saver = new ProjectSaver();
		saver.saveRecentProject();

		return true;
	}
}
