package org.caleydo.rcp.views;


import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.caleydo.rcp.util.search.SearchBar;

public class GLBucket3DView 
extends AGLViewPart {

	public static final String ID = "org.caleydo.rcp.views.GLBucket3DView";

	protected int iGLCanvasDirectorId;
	
	/**
	 * Constructor.
	 */
	public GLBucket3DView() {
		
		super();
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		super.createPartControlSWT(parent);
		
		contributeToActionBars();
	}
	
	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {		

		manager.add(new Separator());
	}
	
	protected void fillLocalToolBar(IToolBarManager manager) {

		IContributionItem searchBar = 
			new SearchBar("Quick search");

		manager.add(new Separator());
		manager.add(searchBar);
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
	}
}