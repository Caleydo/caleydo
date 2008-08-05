package org.caleydo.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor
	extends WorkbenchWindowAdvisor
{

	/**
	 * Constructor.
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor
	 * (org.eclipse.ui.application.IActionBarConfigurer)
	 */
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{

		return new ApplicationActionBarAdvisor(configurer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
	 */
	public void preWindowOpen()
	{

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowProgressIndicator(false);

		/**
		 * Top Level Menu
		 */
		configurer.setShowMenuBar(true);
	}

	/*
     * 
     */
	protected void fillMenuBar(IMenuManager menuBar)
	{
		MenuManager geneviewMenu = new MenuManager("&Caleydo", "caleydo");
		MenuManager helpMenu = new MenuManager("&Help", "help");
		// helpMenu.add(aboutAction);
		menuBar.add(geneviewMenu);
		menuBar.add(helpMenu);
	}
}
