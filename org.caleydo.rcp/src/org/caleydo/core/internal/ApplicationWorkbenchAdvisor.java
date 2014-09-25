/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.core.gui.perspective.GenomePerspective;
import org.caleydo.core.gui.perspective.PartListener;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ViewManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public IWorkbenchConfigurer getWorkbenchConfigurer() {
		return super.getWorkbenchConfigurer();
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return GenomePerspective.PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

	@Override
	public boolean preShutdown() {
		super.preShutdown();
		

		// stop animating
		ViewManager.get().stopAnimator();
		// GeneralManager.get().getViewManager().getDisplayLoopExecution()
		// .stopMultipleExecution(autoSaver);
		// autoSaver = null;

		if (MyPreferences.saveRecentProject()) {
			try {
				new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false,
						ProjectManager.saveRecentProject());
			} catch (InvocationTargetException | InterruptedException e) {
				Logger.create(Application.class).error("can't save recent project", e);
			}
		}

		return true;
	}

	private class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
		/**
		 * Constructor.
		 */
		public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
			super(configurer);
		}

		@Override
		public IWorkbenchWindowConfigurer getWindowConfigurer() {
			return super.getWindowConfigurer();
		}

		@Override
		public void preWindowOpen() {
			IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
			configurer.setShowCoolBar(false);
			configurer.setShowStatusLine(false);
			configurer.setShowFastViewBars(true);
			configurer.getWindow().getPartService().addPartListener(new PartListener());
		}

		@Override
		public void postWindowOpen() {

			super.postWindowOpen();

			// removeNonCaleydoMenuEntries(getWindowConfigurer());

			Application.get().postWorkbenchOpen(getWindowConfigurer());
		}
	}
}
