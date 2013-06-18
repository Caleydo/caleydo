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
package org.caleydo.core.internal;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.perspective.GenomePerspective;
import org.caleydo.core.gui.perspective.PartListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.startup.IStartupProcedure;
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

import com.google.common.base.Function;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private IStartupProcedure startup;

	public ApplicationWorkbenchAdvisor(IStartupProcedure startup) {
		this.startup = startup;
	}
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
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

			// Init the core because the workbench must already be initialized for
			// the XML startup progress bar

			configurer.setTitle("Caleydo - unsaved project");
			startup.run(new Function<String, Void>() {
				@Override
				public Void apply(String data) {
					configurer.setTitle(data);
					return null;
				}
			});
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

			removeNonCaleydoMenuEntries(getWindowConfigurer());

			startup.postWorkbenchOpen();
			startup = null; // cleanup not used any longer

			getWindowConfigurer().getWindow().getShell().setMaximized(true);
		}
	}
}
