package org.geneview.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.geneview.rcp.action.file.FileOpenCsvDataFileAction;
import org.geneview.rcp.action.file.FileOpenXmlConfigFileAction;
import org.geneview.rcp.action.update.UpdateAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	protected IWorkbenchAction exitAction;
	
	protected IWorkbenchAction aboutAction;
	
	protected IWorkbenchAction openNewWorkBenchAction;
	
	protected IWorkbenchAction preferencesAction;
	
	protected IWorkbenchAction saveAction;
	
	protected IWorkbenchAction saveAsAction;
	
	protected IWorkbenchAction editCopyAction;
	
	protected IWorkbenchAction printAction;
	
	protected IWorkbenchAction editPasteAction;
	
	protected IWorkbenchAction editCutAction;
	
	protected IWorkbenchAction editDeleteAction;
	
	protected IWorkbenchAction fileLoadXmlConfigAction;
	
	protected IWorkbenchAction windowCloseAction;
	
	protected UpdateAction updateAction;
	
	protected FileOpenCsvDataFileAction fileOpenCsvDataAction;

	/**
	 * FileOpenXmlConfigFileAction extends IWorkbenchAction
	 */
	protected FileOpenXmlConfigFileAction fileOpenXmlConfigAction;
	
	protected IContributionItem viewDynamicLoaded;
	
	
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		
		/**
		 * FILE menu
		 */
		fileOpenXmlConfigAction = new FileOpenXmlConfigFileAction(window);
		register(fileOpenXmlConfigAction);
		
		fileOpenCsvDataAction = new FileOpenCsvDataFileAction(window);
		register(fileOpenCsvDataAction);
		
		saveAction = ActionFactory.SAVE.create(window);		
		register(saveAction);
		
		saveAsAction = ActionFactory.SAVE_AS.create(window);		
		register(saveAsAction);
		
		preferencesAction = ActionFactory.PREFERENCES.create(window);		
		register(preferencesAction);
		
		printAction = ActionFactory.PRINT.create(window);
		register(printAction);
		
		/**
		 * EDIT menu
		 */
		editCopyAction = ActionFactory.COPY.create(window);		
		register(editCopyAction);
		
		editPasteAction = ActionFactory.PASTE.create(window);			
		register(editPasteAction);
		
		editCutAction = ActionFactory.CUT.create(window);
		register(editCutAction);
		
		editDeleteAction = ActionFactory.DELETE.create(window);
		register(editDeleteAction);
		
		/**
		 * VIEW menu
		 */	
		openNewWorkBenchAction = ActionFactory.OPEN_NEW_WINDOW.create(window);		
		register(openNewWorkBenchAction);
	
		viewDynamicLoaded = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		//register((IAction) viewDynamicLoaded);
		
		windowCloseAction = ActionFactory.CLOSE.create(window);
		register(windowCloseAction);
		
		/**
		 * HELP menu
		 */
		aboutAction = ActionFactory.ABOUT.create(window);	
		aboutAction.setText("About GeneView");
		register(aboutAction);
		
		updateAction = new UpdateAction(window);
		updateAction.setText("Update...");
		register(updateAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {

		/**
		 * FILE
		 */
		MenuManager fileMenu = new MenuManager("&File", "file");
		fileMenu.add(fileOpenCsvDataAction);
		fileMenu.add(fileOpenXmlConfigAction);
		fileMenu.add( new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);	
		fileMenu.add( new Separator());
		fileMenu.add(printAction);
		fileMenu.add( new Separator());
		fileMenu.add(preferencesAction);
		fileMenu.add( new Separator());
		fileMenu.add(exitAction);
		
		/**
		 * EDIT
		 */
		MenuManager editMenu = new MenuManager("&Edit", "edit");
		editMenu.add(editCutAction);
		editMenu.add(editCopyAction);
		editMenu.add(editPasteAction);		
		editMenu.add( new Separator());
		editMenu.add(editDeleteAction);
		
		/**
		 * VIEW
		 */
		MenuManager viewMenu = new MenuManager("&View", "view");
		MenuManager viewOpenGLMenu = new MenuManager("Show Open&GL views ..", "create new OpenGL views");
		MenuManager viewTextMenu = new MenuManager("Show text views ..", "create new text views");
		
			/**
			 * VIEW ==> OpenGL
			 */			
			viewOpenGLMenu.add(exitAction);
	
			/**
			 * VIEW ==> Text
			 */
			viewTextMenu.add(exitAction);
			viewTextMenu.add(exitAction);
		
		viewMenu.add(viewDynamicLoaded);
		viewMenu.add(viewOpenGLMenu);
		viewMenu.add(viewTextMenu);
		viewMenu.add( new Separator());	
		viewMenu.add(openNewWorkBenchAction);			
		viewMenu.add(windowCloseAction);		
		
		/**
		 * HELP
		 */
		MenuManager helpMenu = new MenuManager("&Help", "help");
		helpMenu.add(aboutAction);
		helpMenu.add(updateAction);
			
		/**
         * Top Level Menu
         */		
		/* Add sub menus to main menu */		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
	}

	protected void fillCoolBar(ICoolBarManager coolBar) {

		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());		
		toolbar.add(exitAction);
		toolbar.add(new Separator());
		
		coolBar.add(toolbar);
	}
	
	 public void fillActionBars(int flags) {
		 super.fillActionBars(ActionBarAdvisor.FILL_COOL_BAR|ActionBarAdvisor.FILL_MENU_BAR|ActionBarAdvisor.FILL_STATUS_LINE);
	 }
}
