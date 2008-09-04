package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.action.view.TakeSnapshotAction;
import org.caleydo.rcp.action.view.remote.CloseOrResetContainedViews;
import org.caleydo.rcp.action.view.remote.ToggleLayoutAction;
import org.caleydo.rcp.util.search.SearchBar;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class GLRemoteRenderingView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLRemoteRenderingView";
	
	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public GLRemoteRenderingView()
	{
		super();
		
		createToolBarItems(-1);
		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		createGLCanvas();
		iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_HEAT_MAP_3D, -1));
		iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_PARALLEL_COORDINATES_3D, -1));
		createGLEventListener(ECommandType.CREATE_GL_BUCKET_3D, glCanvas.getID());
		
		// Trigger gene/pathway search command
		ArrayList<Integer> iAlReceiverIDs = new ArrayList<Integer>();
		iAlReceiverIDs.add(iGLEventListenerID);
		CmdViewCreateDataEntitySearcher cmd = (CmdViewCreateDataEntitySearcher) GeneralManager.get().getCommandManager()
			.createCommandByType(ECommandType.CREATE_VIEW_DATA_ENTITY_SEARCHER);
		cmd.setAttributes(iAlReceiverIDs);
		cmd.doCommand();
		
		((GLRemoteRendering)GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iGLEventListenerID))
			.setInitialContainedViews(iAlContainedViewIDs);
	}
	
	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();
		
		IAction takeSnapshotAction = new TakeSnapshotAction(iViewID);
		alToolbar.add(takeSnapshotAction);	
		IAction closeOrResetContainedViews = new CloseOrResetContainedViews(iViewID);
		alToolbar.add(closeOrResetContainedViews);		
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
	
	@Override
	public void dispose()
	{
		super.dispose();

		for (Integer iContainedViewID : iAlContainedViewIDs)
		{
			GeneralManager.get().getViewGLCanvasManager()
				.unregisterGLEventListener(iContainedViewID);			
		}
		
		//TODO: cleanup data entity searcher view
	}
}