package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.rcp.action.view.TakeSnapshotAction;
import org.caleydo.rcp.action.view.remote.EraseAction;
import org.caleydo.rcp.action.view.remote.ToggleLayoutAction;
import org.caleydo.rcp.util.search.SearchBar;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class GLRemoteRenderingView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLRemoteRenderingView";

/**
	 * Constructor.
	 */
	public GLRemoteRenderingView()
	{
		super();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
	}

	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();
		
		IAction takeSnapshotAction = new TakeSnapshotAction(iViewID);
		alToolbar.add(takeSnapshotAction);	
		IAction eraseAction = new EraseAction(iViewID);
		alToolbar.add(eraseAction);		
		IAction toggleLayoutAction = new ToggleLayoutAction(iViewID);
		alToolbar.add(toggleLayoutAction);
	}
	
	protected final void fillToolBar()
	{
		if (alToolbar == null)
		{
			createToolBarItems(iGLEventListenerID);
		}
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();	
		fillToolBar(toolBarManager);
	}
	
	/**
	 * Overloads static fillToolBar method in AGLViewPart because 
	 * the search bar must be added in a different way as usual toolbar items.
	 * 
	 * @param toolBarManager
	 */
	public static void fillToolBar(final IToolBarManager toolBarManager)
	{		
		// Add search bar
		toolBarManager.add(new SearchBar("Quick search"));

		for (IAction toolBarAction : alToolbar)
		{
			toolBarManager.add(toolBarAction);			
		}
	}
}