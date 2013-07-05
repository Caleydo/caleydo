/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.perspective.GenomePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IActionBarConfigurer;
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
		GeneralManager.get().getViewManager().stopAnimator();
		// GeneralManager.get().getViewManager().getDisplayLoopExecution()
		// .stopMultipleExecution(autoSaver);
		// autoSaver = null;

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

	/**
	 * Removing all non Caleydo menus. Especially useful for Eclipse contributed plugins when starting Caleydo from
	 * Eclipses
	 */
	private static void removeNonCaleydoMenuEntries(IWorkbenchWindowConfigurer wconfigurer) {
		IMenuManager menuManager = wconfigurer.getActionBarConfigurer().getMenuManager();
		for (IContributionItem item : menuManager.getItems()) {

			if (!item.getId().contains("org.caleydo")) {
				menuManager.remove(item);
			}
		}

		if (DataDomainManager.get().getDataDomainByID("org.caleydo.datadomain.generic") != null) {

			IActionBarConfigurer configurer = wconfigurer.getActionBarConfigurer();

			// Delete unwanted menu items
			IContributionItem[] menuItems = configurer.getMenuManager().getItems();
			for (int i = 0; i < menuItems.length; i++) {
				IContributionItem menuItem = menuItems[i];
				if (menuItem.getId().equals("org.caleydo.search.menu")) {
					configurer.getMenuManager().remove(menuItem);
				} else if (menuItem.getId().equals("viewMenu")) {
					IContributionItem itemToRemove = ((MenuManager) menuItem)
							.find("org.caleydo.core.command.openviews.remote");

					if (itemToRemove != null)
						itemToRemove.setVisible(false);
				}
			}
		}
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
			// configurer.getWindow().getPartService().addPartListener(new PartListener());
		}

		@Override
		public void postWindowOpen() {

			super.postWindowOpen();

			removeNonCaleydoMenuEntries(getWindowConfigurer());

			Application.get().postWorkbenchOpen(getWindowConfigurer());
		}
	}
}
