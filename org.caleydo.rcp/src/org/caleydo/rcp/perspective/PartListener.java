package org.caleydo.rcp.perspective;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.views.opengl.AGLViewPart;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

public class PartListener
	implements IPartListener2
{
	@Override
	public void partClosed(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;

		if (glView == null)
			return;

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return;

		// Remove view specific toolbar from general toolbar view
		ToolBarView toolBarView = ((ToolBarView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(ToolBarView.ID));

		if (toolBarView == null)
			return;

		toolBarView.removeViewSpecificToolBar(glView.getGLEventListenerID());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPage().getActivePart();

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;

		final IToolBarManager toolBarManager = glView.getViewSite().getActionBars()
				.getToolBarManager();

		toolBarManager.removeAll();

		// Check if view is inside the workbench or detached to a separate
		// window
		if (!partRef.getPage().getActivePart().getSite().getShell().getText()
				.equals("Caleydo"))
		{
			IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();
			AGLEventListener glEventListener = viewGLCanvasManager.getGLEventListener(glView
					.getGLEventListenerID());

			createViewSpecificToolbar(glEventListener, toolBarManager);

			if (glEventListener instanceof GLRemoteRendering)
			{
				AGLEventListener glSubEventListener;
				GLRemoteRenderingView.createToolBarItems(glView.getGLEventListenerID());
				glView.fillToolBar();

				// Add toolbars of remote rendered views to remote view toolbar
				for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) glEventListener)
						.getRemoteRenderedViews())
				{
					glSubEventListener = viewGLCanvasManager
							.getGLEventListener(iRemoteRenderedGLViewID);
					toolBarManager.add(new Separator());
					createViewSpecificToolbar(glSubEventListener, toolBarManager);
				}
			}

			// Remove view specific toolbar from general toolbar view
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(ToolBarView.ID))
					.removeViewSpecificToolBar(glView.getGLEventListenerID());
		}
		else
		{
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(ToolBarView.ID)).addViewSpecificToolBar(glView
					.getGLEventListenerID());
		}

		glView.getSWTComposite().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				toolBarManager.update(true);
			}
		});
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;

		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID)).addViewSpecificToolBar(glView
				.getGLEventListenerID());
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPage().getActivePart();

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;

		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID)).highlightViewSpecificToolBar(glView
				.getGLEventListenerID());
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef)
	{

	}

	private void createViewSpecificToolbar(AGLEventListener glEventListener,
			IToolBarManager toolBarManager)
	{
		int iGLEvenntListenerID = glEventListener.getID();

		if (glEventListener instanceof GLPathway)
		{
			GLPathwayView.createToolBarItems(iGLEvenntListenerID);
			GLPathwayView.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLHeatMap)
		{
			GLHeatMapView.createToolBarItems(iGLEvenntListenerID);
			GLHeatMapView.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLHierarchicalHeatMap)
		{
			GLHierarchicalHeatMapView.createToolBarItems(iGLEvenntListenerID);
			GLHierarchicalHeatMapView.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLParallelCoordinates)
		{
			GLParCoordsView.createToolBarItems(iGLEvenntListenerID);
			GLParCoordsView.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLGlyph)
		{
			GLGlyphView.createToolBarItems(iGLEvenntListenerID);
			GLGlyphView.fillToolBar(toolBarManager);
		}
	}
}
