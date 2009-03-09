package org.caleydo.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor
	extends ActionBarAdvisor {

	// private IContributionItem viewList;
	// private MenuManager showViewMenuManager;
	public static IStatusLineManager statusLineManager;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		//        
		statusLineManager = configurer.getStatusLineManager();
	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		super.makeActions(window);
		//    	
		// aboutAction = ActionFactory.ABOUT.create(window);
		// register(aboutAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);
		//    	
		// MenuManager helpMenu = new MenuManager("&Help", "help");
		// helpMenu.add(aboutAction);
		// menuBar.insertAfter("window", helpMenu);
	}
}
