package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IViewReference;
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
			throw new IllegalStateException("Cannot execute exit command.");
		}
	}

	@Override
	public void setActiveGLSubView(AGLEventListener parentGLEventListener,
			AGLEventListener subGLEventListener)
	{
		for (IViewReference rcpView : PlatformUI.getWorkbench().getWorkbenchWindows()[0]
				.getActivePage().getViewReferences())
		{
			if (!rcpView.getId().equals(GLRemoteRenderingView.ID))
				continue;

			GLRemoteRenderingView remoteRenderingRCPView = (GLRemoteRenderingView) rcpView
					.getView(false);

			final IToolBarManager toolBarManager = remoteRenderingRCPView.getViewSite()
					.getActionBars().getToolBarManager();
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
				else if (subGLEventListener instanceof GLHierarchicalHeatMap)
				{
					GLHierarchicalHeatMapView.createToolBarItems(subGLEventListener.getID());
					GLHierarchicalHeatMapView.fillToolBar(toolBarManager);
				}
				else if (subGLEventListener instanceof GLParallelCoordinates)
				{
					GLParCoordsView.createToolBarItems(subGLEventListener.getID());
					GLParCoordsView.fillToolBar(toolBarManager);
				}
				else if (subGLEventListener instanceof GLGlyph)
				{
					GLGlyphView.createToolBarItems(subGLEventListener.getID());
					GLGlyphView.fillToolBar(toolBarManager);
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

	@Override
	public void setShortInfo(String sMessage)
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
}
