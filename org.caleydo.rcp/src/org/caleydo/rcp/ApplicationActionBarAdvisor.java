package org.caleydo.rcp;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

//	private IContributionItem viewList;
//	private MenuManager showViewMenuManager; 
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
//        
//        configurer.getStatusLineManager().add(action)
    }

//    @Override
//    protected void makeActions(IWorkbenchWindow window)
//    {
//    	super.makeActions(window);
//    	
//    	showViewMenuManager = new MenuManager("Show View", "showView");
//        viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
//        viewList.
//    }
//
//    @Override
//    protected void fillMenuBar(IMenuManager menuBar)
//    {
//    	super.fillMenuBar(menuBar);
//    	
//        MenuManager windowMenu = new MenuManager("&Bla", "bla");
//        showViewMenuManager.add(viewList);
//        windowMenu.add(showViewMenuManager); 
//        menuBar.add(windowMenu);
//    }
}
