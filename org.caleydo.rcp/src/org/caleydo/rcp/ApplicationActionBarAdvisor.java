package org.caleydo.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    }
    
}


//public class ApplicationActionBarAdvisor
//	extends ActionBarAdvisor
//{
//	protected IWorkbenchAction aboutAction;
//
//	protected IWorkbenchAction openNewWorkBenchAction;
//
//	protected IWorkbenchAction preferencesAction;
//
//	// protected IWorkbenchAction saveAction;
//
//	// protected IWorkbenchAction saveAsAction;
//
//	// protected IWorkbenchAction editCopyAction;
//
//	protected IWorkbenchAction printAction;
//
//	// protected IWorkbenchAction editPasteAction;
//
//	// protected IWorkbenchAction editCutAction;
//
//	// protected IWorkbenchAction editDeleteAction;
//
//	protected OpenSearchDataEntityAction editOpenSearchDataEntityAction;
//
//	protected IWorkbenchAction fileLoadXmlConfigAction;
//
//	protected IWorkbenchAction windowCloseAction;
//
//	protected UpdateAction updateAction;
//
//	protected FileLoadDataAction fileLoadDataAction;
//
//	protected FileOpenProjectAction fileOpenProjectAction;
//
//	protected FileSaveProjectAction fileSaveXmlConfigAction;
//
//	protected IContributionItem viewDynamicLoaded;
//
//	/**
//	 * Constructor.
//	 */
//	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
//	{
//		super(configurer);
//	}

//	protected void makeActions(IWorkbenchWindow window)
//	{
//		/**
//		 * FILE menu
//		 */
//		fileOpenProjectAction = new FileOpenProjectAction(window.getShell());
//		register(fileOpenProjectAction);
//
//		fileLoadDataAction = new FileLoadDataAction(window);
//		register(fileLoadDataAction);
//
//		fileSaveXmlConfigAction = new FileSaveProjectAction(window.getShell());
//		register(fileSaveXmlConfigAction);
//
//		preferencesAction = ActionFactory.PREFERENCES.create(window);
//		register(preferencesAction);
//
//		printAction = ActionFactory.PRINT.create(window);
//		register(printAction);
//
//		/**
//		 * EDIT menu
//		 */
//		// editCopyAction = ActionFactory.COPY.create(window);
//		// register(editCopyAction);
//		//		
//		// editPasteAction = ActionFactory.PASTE.create(window);
//		// register(editPasteAction);
//		//		
//		// editCutAction = ActionFactory.CUT.create(window);
//		// register(editCutAction);
//		//		
//		// editDeleteAction = ActionFactory.DELETE.create(window);
//		// register(editDeleteAction);
//		editOpenSearchDataEntityAction = new OpenSearchDataEntityAction(window);
//		register(editOpenSearchDataEntityAction);
//
//		/**
//		 * SEARCH menu
//		 */
//
//		/**
//		 * VIEW menu
//		 */
//		openNewWorkBenchAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
//		register(openNewWorkBenchAction);
//
//		viewDynamicLoaded = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
//		// register((IAction) viewDynamicLoaded);
//
//		windowCloseAction = ActionFactory.CLOSE.create(window);
//		register(windowCloseAction);
//
//		/**
//		 * HELP menu
//		 */
//		aboutAction = ActionFactory.ABOUT.create(window);
//		aboutAction.setText("About Caleydo");
//		register(aboutAction);
//
//		updateAction = new UpdateAction(window);
//		updateAction.setText("Update...");
//		register(updateAction);
//	}

//	protected void fillMenuBar(IMenuManager menuBar)
//	{
//
//		/**
//		 * FILE
//		 */
//		MenuManager fileMenu = new MenuManager("&File", "file");
//		fileMenu.add(fileLoadDataAction);
//		fileMenu.add(fileOpenProjectAction);
//		fileMenu.add(fileSaveXmlConfigAction);
//		fileMenu.add(new Separator());
//		fileMenu.add(printAction);
//		fileMenu.add(new Separator());
//		fileMenu.add(preferencesAction);
//
//		/**
//		 * EDIT
//		 */
//		MenuManager editMenu = new MenuManager("&Edit", "edit");
//		// editMenu.add(editCutAction);
//		// editMenu.add(editCopyAction);
//		// editMenu.add(editPasteAction);
//		// editMenu.add( new Separator());
//		// editMenu.add(editDeleteAction);
//		editMenu.add(editOpenSearchDataEntityAction);
//
//		/**
//		 * VIEW
//		 */
//		MenuManager viewMenu = new MenuManager("&View", "view");
//		MenuManager viewOpenGLMenu = new MenuManager("Show Open&GL views ..",
//				"create new OpenGL views");
//		MenuManager viewTextMenu = new MenuManager("Show text views ..",
//				"create new text views");
//
//		viewMenu.add(viewDynamicLoaded);
//		viewMenu.add(viewOpenGLMenu);
//		viewMenu.add(viewTextMenu);
//		viewMenu.add(new Separator());
//		viewMenu.add(openNewWorkBenchAction);
//		viewMenu.add(windowCloseAction);
//
//		/**
//		 * HELP
//		 */
//		MenuManager helpMenu = new MenuManager("&Help", "help");
//		helpMenu.add(aboutAction);
//		helpMenu.add(updateAction);
//
//		/**
//		 * Top Level Menu
//		 */
//		/* Add sub menus to main menu */
//		menuBar.add(fileMenu);
//		menuBar.add(editMenu);
//		menuBar.add(viewMenu);
//		menuBar.add(helpMenu);
//	}
//
//	protected void fillCoolBar(ICoolBarManager coolBar)
//	{
//
////		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
////		toolbar.add(new Separator());
////
////		coolBar.add(toolbar);
//	}
//
//	public void fillActionBars(int flags)
//	{
//		super.fillActionBars(ActionBarAdvisor.FILL_COOL_BAR | ActionBarAdvisor.FILL_MENU_BAR
//				| ActionBarAdvisor.FILL_STATUS_LINE);
//	}
//}
