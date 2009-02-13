package org.caleydo.rcp.perspective;

import java.util.ArrayList;
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
//	private ArrayList<IWorkbenchPart> alDetachedWorkbenches;
//	private IWorkbenchPart lastDetachedWorkbenchPart;
//	private IWorkbenchPart lastOpenedWorkbenchPart;
//	private IWorkbenchPart lastHiddenWorkbenchPart;
	
	/**
	 * Constructor.
	 */
	public PartListener()
	{
//		alDetachedWorkbenches = new ArrayList<IWorkbenchPart>();
	}
	
	@Override
	public void partOpened(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);
		
		System.out.println("Opened:" +activePart);

		if (!(activePart instanceof AGLViewPart))
			return;
		
		AGLViewPart glView = (AGLViewPart) activePart;
		
		ToolBarView toolBarView = ((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID));
		toolBarView.removeAllViewSpecificToolBars();
		toolBarView.addViewSpecificToolBar(glView
				.getGLEventListener().getID());
		
//		lastOpenedWorkbenchPart = activePart;
	}
	
	@Override
	public void partClosed(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);

		System.out.println("Closed:" +activePart);
		
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

		toolBarView.removeViewSpecificToolBar(glView.getGLEventListener().getID());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);

		System.out.println("Visible:" +activePart);
		
		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;
		
		GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(
				glView.getGLCanvas().getID());

//		if (lastOpenedWorkbenchPart != null)
//			return;
//		
//		if (lastHiddenWorkbenchPart == activePart)
//		{
//			lastHiddenWorkbenchPart = null;
//			return;
//		}
		
		final IToolBarManager toolBarManager = glView.getViewSite().getActionBars()
				.getToolBarManager();

		toolBarManager.removeAll();
		
		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID)).removeAllViewSpecificToolBars();

		// Check if view is inside the workbench or detached to a separate
		// window
		if (!partRef.getPage().getActivePart().getSite().getShell().getText()
				.equals("Caleydo"))
		{
//			alDetachedWorkbenches.add(activePart);
//			lastDetachedWorkbenchPart = activePart;
			
			IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();
			AGLEventListener glEventListener = viewGLCanvasManager.getGLEventListener(glView
					.getGLEventListener().getID());

			createViewSpecificToolbar(glEventListener, toolBarManager);

			if (glEventListener instanceof GLRemoteRendering)
			{
				AGLEventListener glSubEventListener;
				GLRemoteRenderingView.createToolBarItems(glView.getGLEventListener().getID());
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

//			// Remove view specific toolbar from general toolbar view
//			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
//					.getActivePage().findView(ToolBarView.ID))
//					.removeViewSpecificToolBar(glView.getGLEventListenerID());
		}
		else
		{
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(ToolBarView.ID)).addViewSpecificToolBar(glView
							.getGLEventListener().getID());
			
//			alDetachedWorkbenches.remove(activePart);
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
		IWorkbenchPart activePart = partRef.getPart(false);

		System.out.println("Deactivate:" +activePart);
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef)
	{	
		IWorkbenchPart activePart = partRef.getPart(false);
		
		System.out.println("Hidden:" +activePart);
		
		if (!(activePart instanceof AGLViewPart))
			return;

//		if (alDetachedWorkbenches.contains(activePart))
//			return;
//		
//		if (lastDetachedWorkbenchPart == activePart)
//		{
//			lastDetachedWorkbenchPart = null;
//			return;
//		}
//		
//		lastHiddenWorkbenchPart = activePart;
		
		AGLViewPart glViewPart = (AGLViewPart) activePart;

		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvasFromAnimator(
				glViewPart.getGLCanvas().getID());
		
		// Check if view is inside the workbench or detached to a separate
		// window
		if (partRef.getPage().getActivePart().getSite().getShell().getText()
				.equals("Caleydo"))
		{
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(ToolBarView.ID)).removeViewSpecificToolBar(glViewPart
							.getGLEventListener().getID());
		}	
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart activePart = partRef.getPart(false);

		System.out.println("Activated:" +activePart);
		
//		lastOpenedWorkbenchPart = null;
		
		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;
		
		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID)).removeAllViewSpecificToolBars();
		
		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ToolBarView.ID)).addViewSpecificToolBar(glView
				.getGLEventListener().getID());
		
//		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.findView(ToolBarView.ID)).highlightViewSpecificToolBar(glView
//				.getGLEventListenerID());
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef)
	{
		System.out.println("Part brought to top");
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
