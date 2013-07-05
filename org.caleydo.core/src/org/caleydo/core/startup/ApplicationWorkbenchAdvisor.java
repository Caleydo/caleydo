/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.startup;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.AutoSaver;
import org.caleydo.core.serialize.ProjectManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Display;
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
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);

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

//		autoSaver = new AutoSaver();
//		GeneralManager.get().getViewManager().getDisplayLoopExecution()
//				.executeMultiple(autoSaver);
	}

	private void filterPreferencePages() {

		if (GeneralManager.RELEASE_MODE) {
			PreferenceManager preferenceManager = this.getWorkbenchConfigurer().getWorkbench()
					.getPreferenceManager();

			for (Object node : preferenceManager.getElements(PreferenceManager.PRE_ORDER)) {

				IPreferenceNode prefNode = (IPreferenceNode) node;
				if (!prefNode.getId().contains("org.caleydo.core")) {
					preferenceManager.remove(prefNode);
				}
			}
		}
	}

	@Override
	public boolean preShutdown() {
		super.preShutdown();

		// stop animating
		GeneralManager.get().getViewManager().stopAnimator();
//		GeneralManager.get().getViewManager().getDisplayLoopExecution()
//				.stopMultipleExecution(autoSaver);
//		autoSaver = null;

		try {
			new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false,
					ProjectManager.saveRecentProject());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}
}
