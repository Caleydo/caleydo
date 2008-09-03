package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.rcp.views.GLHeatMapView;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLPathwayView;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public class RCPBridge
implements IGUIBridge
{
	@Override
	public void closeApplication()
	{
		try
		{
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e)
		{
			throw new CaleydoRuntimeException("Cannot execute exit command.", 
					CaleydoRuntimeExceptionType.GUI_RCP);
		}
	}
	
	@Override
	public void setActiveGLSubView(AGLEventListener parentGLEventListener,
			AGLEventListener subGLEventListener)
	{	
		for (IWorkbenchPage rcpView :	PlatformUI.getWorkbench()
				.getWorkbenchWindows()[0].getPages())
		{
			if (!(rcpView instanceof GLRemoteRenderingView))
				continue;

			GLRemoteRenderingView remoteRenderingRCPView = (GLRemoteRenderingView)rcpView;
			
			final IToolBarManager toolBarManager = remoteRenderingRCPView.getViewSite().getActionBars().getToolBarManager();
			toolBarManager.removeAll();
					
			GLRemoteRenderingView.createToolBarItems(parentGLEventListener.getID());
			GLRemoteRenderingView.fillToolBar(toolBarManager);
		
			toolBarManager.add(new Separator());
			
			if (parentGLEventListener instanceof GLRemoteRendering)
			{						
				if (subGLEventListener instanceof GLPathway)
				{
					GLPathwayView.createToolBarItems(subGLEventListener.getID());
					GLPathwayView.fillToolBar(toolBarManager);
				}
				else if (subGLEventListener instanceof GLHeatMap)
				{
					GLHeatMapView.createToolBarItems(subGLEventListener.getID());
					GLHeatMapView.fillToolBar(toolBarManager);
				}
				else if (subGLEventListener instanceof GLParallelCoordinates)
				{
					GLParCoordsView.createToolBarItems(subGLEventListener.getID());
					GLParCoordsView.fillToolBar(toolBarManager);
				}
			}
			
			remoteRenderingRCPView.getSWTComposite().getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					toolBarManager.update(true);
				}
			});
		}
	}
}
