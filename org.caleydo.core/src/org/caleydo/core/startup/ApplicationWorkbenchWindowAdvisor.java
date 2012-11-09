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

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.perspective.PartListener;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor
	extends WorkbenchWindowAdvisor {

	/**
	 * This is the only point where we get the workbench window configurer. We
	 * cache it in order to be able to change the window title during runtime.
	 */
	private static IWorkbenchWindowConfigurer configurer;

	private static String windowTitle;

	/**
	 * Constructor.
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);

		// Init the core because the workbench must already be initialized for
		// the XML startup progress bar
		StartupProcessor.get().initCore();
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {

		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		configurer = getWindowConfigurer();
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.getWindow().getPartService().addPartListener(new PartListener());
		// configurer.setShowProgressIndicator(true);
		// configurer.setShowPerspectiveBar(true);
	}

	@Override
	public void postWindowCreate() {
		super.postWindowCreate();

		configurer.getWindow().getShell().setMaximized(true);

		// If the title was not set during startup (e.g. when a project is
		// loaded), the default title is set
		if (windowTitle == null)
			setWindowTitle("Caleydo - unsaved project");
		else
			setWindowTitle(windowTitle);

		removeNonCaleydoMenuEntries();

		StartupProcessor.get().getStartupProcedure().postWorkbenchOpen();
	}

	/**
	 * Removing all non Caleydo menus. Especially useful for Eclipse contributed
	 * plugins when starting Caleydo from Eclipses
	 */
	private void removeNonCaleydoMenuEntries() {
		IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer()
				.getMenuManager();
		for (IContributionItem item : menuManager.getItems()) {

			if (!item.getId().contains("org.caleydo")) {
				menuManager.remove(item);
			}
		}

		if (DataDomainManager.get().getDataDomainByID("org.caleydo.datadomain.generic") != null) {

			IActionBarConfigurer configurer = getWindowConfigurer().getActionBarConfigurer();

			// Delete unwanted menu items
			IContributionItem[] menuItems = configurer.getMenuManager().getItems();
			for (int i = 0; i < menuItems.length; i++) {
				IContributionItem menuItem = menuItems[i];
				if (menuItem.getId().equals("org.caleydo.search.menu")) {
					configurer.getMenuManager().remove(menuItem);
				}
				else if (menuItem.getId().equals("viewMenu")) {
					IContributionItem itemToRemove = ((MenuManager) menuItem)
							.find("org.caleydo.core.command.openviews.remote");

					if (itemToRemove != null)
						itemToRemove.setVisible(false);
				}
			}
		}
	}

	public static void setWindowTitle(String title) {

		windowTitle = title;

		if (configurer != null)
			configurer.setTitle(windowTitle);
	}
}
