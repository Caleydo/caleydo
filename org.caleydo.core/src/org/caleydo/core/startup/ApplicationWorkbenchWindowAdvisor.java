package org.caleydo.core.startup;

import org.caleydo.core.gui.perspective.PartListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
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
	 * Constructor.
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);

		// Init the core because the workbench must already be initialized for the XML startup progress bar
		StartupProcessor.get().initCore();
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {

		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		// configurer.setShowProgressIndicator(true);
		configurer.setTitle("Caleydo");

		configurer.getWindow().getPartService().addPartListener(new PartListener());
	}

	@Override
	public void postWindowCreate() {
		super.postWindowCreate();

		getWindowConfigurer().getWindow().getShell().setMaximized(true);

		getWindowConfigurer().getActionBarConfigurer().getMenuManager();

		IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		for (IContributionItem item : menuManager.getItems()) {

			// Removing all non Caleydo menus.
			// Espically useful for Eclipse contributed plugins when starting Caleydo from Eclipses
			if (!item.getId().contains("org.caleydo")) {
				menuManager.remove(item);
			}
		}

		// Set status line in caleydo core
		GeneralManager
			.get()
			.getSWTGUIManager()
			.setExternalRCPStatusLine(getWindowConfigurer().getActionBarConfigurer().getStatusLineManager(),
				getWindowConfigurer().getWindow().getShell().getDisplay());

		if (DataDomainManager.get().getDataDomain("org.caleydo.datadomain.generic") != null) {

			IActionBarConfigurer configurer = getWindowConfigurer().getActionBarConfigurer();

			// Delete unwanted menu items
			IContributionItem[] menuItems = configurer.getMenuManager().getItems();
			for (int i = 0; i < menuItems.length; i++) {
				IContributionItem menuItem = menuItems[i];
				if (menuItem.getId().equals("org.caleydo.search.menu")) {
					configurer.getMenuManager().remove(menuItem);
				}
				else if (menuItem.getId().equals("viewMenu")) {
					IContributionItem itemToRemove =
						((MenuManager) menuItem).find("org.caleydo.core.command.openviews.remote");

					if (itemToRemove != null)
						itemToRemove.setVisible(false);
				}
			}
		}
	}
}
