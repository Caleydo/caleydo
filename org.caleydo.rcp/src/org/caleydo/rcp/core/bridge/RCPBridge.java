package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class RCPBridge
	implements IGUIBridge
{
	private String sFileNameCurrentDataSet;

	@Override
	public void closeApplication()
	{
		try
		{
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e)
		{
			throw new IllegalStateException("Cannot execute exit command.");
		}
	}

	// @Override
	// public void setActiveGLSubView(AGLEventListener parentGLEventListener,
	// AGLEventListener subGLEventListener)
	// {
	// for (IViewReference rcpView :
	// PlatformUI.getWorkbench().getWorkbenchWindows()[0]
	// .getActivePage().getViewReferences())
	// {
	// if (!rcpView.getId().equals(GLRemoteRenderingView.ID))
	// continue;
	//
	// GLRemoteRenderingView remoteRenderingRCPView = (GLRemoteRenderingView)
	// rcpView
	// .getView(false);
	//
	// final IToolBarManager toolBarManager =
	// remoteRenderingRCPView.getViewSite()
	// .getActionBars().getToolBarManager();
	// toolBarManager.removeAll();
	//
	// GLRemoteRenderingView.createToolBarItems(parentGLEventListener.getID());
	// GLRemoteRenderingView.fillToolBar(toolBarManager);
	//
	// toolBarManager.add(new Separator());
	//
	// if (parentGLEventListener instanceof GLRemoteRendering)
	// {
	// if (subGLEventListener instanceof GLPathway)
	// {
	// GLPathwayView.createToolBarItems(subGLEventListener.getID());
	// GLPathwayView.fillToolBar(toolBarManager);
	// }
	// else if (subGLEventListener instanceof GLHeatMap)
	// {
	// GLHeatMapView.createToolBarItems(subGLEventListener.getID());
	// GLHeatMapView.fillToolBar(toolBarManager);
	// }
	// else if (subGLEventListener instanceof GLHierarchicalHeatMap)
	// {
	// GLHierarchicalHeatMapView.createToolBarItems(subGLEventListener.getID());
	// GLHierarchicalHeatMapView.fillToolBar(toolBarManager);
	// }
	// else if (subGLEventListener instanceof GLParallelCoordinates)
	// {
	// GLParCoordsView.createToolBarItems(subGLEventListener.getID());
	// GLParCoordsView.fillToolBar(toolBarManager);
	// }
	// else if (subGLEventListener instanceof GLGlyph)
	// {
	// GLGlyphView.createToolBarItems(subGLEventListener.getID());
	// GLGlyphView.fillToolBar(toolBarManager);
	// }
	// }
	//
	// remoteRenderingRCPView.getSWTComposite().getDisplay().asyncExec(new
	// Runnable()
	// {
	// public void run()
	// {
	// toolBarManager.update(true);
	// }
	// });
	// }
	// }

	@Override
	public void setShortInfo(String sMessage)
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void setFileNameCurrentDataSet(String sFileName)
	{
		this.sFileNameCurrentDataSet = sFileName;
	}

	@Override
	public String getFileNameCurrentDataSet()
	{
		return sFileNameCurrentDataSet;
	}
}
