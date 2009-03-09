package org.caleydo.rcp.perspective;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.views.CaleydoViewPart;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

/**
 * Listener for events that are related to view changes (detach, visible, hide, activate, etc.)
 * 
 * @author Marc Streit
 */
public class PartListener
	implements IPartListener2 {
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glView = (AGLViewPart) activePart;

		if (glView == null)
			return;

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return;

		// Remove view specific toolbar from general toolbar view
		ToolBarView toolBarView =
			(ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
				ToolBarView.ID);

		if (toolBarView == null)
			return;

		toolBarView.removeViewSpecificToolBar(glView.getGLEventListener().getID());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		// System.out.println("Visible: " +partRef.getTitle());

		if (!(activePart instanceof CaleydoViewPart))
			return;

		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;

		if (viewPart instanceof AGLViewPart) {
			GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(
				((AGLViewPart) viewPart).getGLCanvas().getID());
		}

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return;

		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();

		toolBarManager.removeAll();

		((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
			ToolBarView.ID)).removeAllViewSpecificToolBars();

		// Check if view is inside the workbench or detached to a separate
		// window
		if (!activePart.getSite().getShell().getText().equals("Caleydo")) {
			createViewSpecificToolbar(viewPart, toolBarManager);

			if (viewPart instanceof AGLViewPart) {
				IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();
				AGLEventListener glEventListener =
					viewGLCanvasManager.getGLEventListener(((AGLViewPart) viewPart).getGLEventListener().getID());

				if (glEventListener instanceof GLRemoteRendering) {
					AGLEventListener glSubEventListener;
					GLRemoteRenderingView.createToolBarItems(viewPart.getViewID());
					viewPart.fillToolBar();

					// Add toolbars of remote rendered views to remote view
					// toolbar
					for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) glEventListener).getRemoteRenderedViews()) {
						glSubEventListener = viewGLCanvasManager.getGLEventListener(iRemoteRenderedGLViewID);
						toolBarManager.add(new Separator());
						createViewSpecificToolbar(glSubEventListener, toolBarManager);
					}
				}
			}
		}
		else {
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
				ToolBarView.ID)).addViewSpecificToolBar(viewPart);
		}

		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				toolBarManager.update(true);
			}
		});
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		// System.out.println("Hide: " +partRef.getTitle());

		if (!(activePart instanceof AGLViewPart))
			return;

		AGLViewPart glViewPart = (AGLViewPart) activePart;

		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvasFromAnimator(
			glViewPart.getGLCanvas().getID());

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return;

		// Check if view is inside the workbench or detached to a separate
		// window
		if (activePart.getSite().getShell().getText().equals("Caleydo") && glViewPart != null) {
			((ToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
				ToolBarView.ID)).removeViewSpecificToolBar(glViewPart.getGLEventListener().getID());
		}
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	private void createViewSpecificToolbar(CaleydoViewPart viewPart, IToolBarManager toolBarManager) {
		if (viewPart instanceof AGLViewPart) {
			AGLEventListener glEventListener = ((AGLViewPart) viewPart).getGLEventListener();
			createViewSpecificToolbar(glEventListener, toolBarManager);
		}
		// else if (viewPart instanceof HTMLBrowserView)
		// {
		// // HTMLBrowserView.createToolBarItems(viewPart.getViewID());
		// // HTMLBrowserView.fillToolBar(toolBarManager);
		// }
	}

	private void createViewSpecificToolbar(AGLEventListener glEventListener, IToolBarManager toolBarManager) {
		int iGLEvenntListenerID = glEventListener.getID();

		if (glEventListener instanceof GLPathway) {
			GLPathwayView.createToolBarItems(iGLEvenntListenerID);
			CaleydoViewPart.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLHeatMap) {
			GLHeatMapView.createToolBarItems(iGLEvenntListenerID);
			CaleydoViewPart.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLHierarchicalHeatMap) {
			GLHierarchicalHeatMapView.createToolBarItems(iGLEvenntListenerID);
			CaleydoViewPart.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLParallelCoordinates) {
			GLParCoordsView.createToolBarItems(iGLEvenntListenerID);
			CaleydoViewPart.fillToolBar(toolBarManager);
		}
		else if (glEventListener instanceof GLGlyph) {
			GLGlyphView.createToolBarItems(iGLEvenntListenerID);
			CaleydoViewPart.fillToolBar(toolBarManager);
		}
	}
}
