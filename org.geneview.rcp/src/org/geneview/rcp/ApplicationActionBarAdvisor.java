package org.geneview.rcp;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	protected IWorkbenchAction exitAction;
	
	protected IWorkbenchAction aboutAction;
	
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		
//		exitAction = ActionFactory.QUIT.create(window);
//		register(exitAction);
//		
//		aboutAction = ActionFactory.ABOUT.create(window);
//		register(aboutAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {

//		MenuManager mainMenu = new MenuManager("&Main", "main");
//		mainMenu.add(aboutAction);
//		mainMenu.add(exitAction);
//		menuBar.add(mainMenu);
	}

	protected void fillCoolBar(ICoolBarManager coolBar) {

//		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
//		coolBar.add(toolbar);
//		toolbar.add(exitAction);
//		toolbar.add(new Separator());
	}
}
