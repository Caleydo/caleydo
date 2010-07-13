package org.caleydo.rcp.perspective;

import java.util.List;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.ViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.IView;
import org.caleydo.rcp.toolbar.RcpToolBarView;
import org.caleydo.rcp.toolbar.ToolBarContentFactory;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

/**
 * Listener for events that are related to view changes (detach, visible, hide, activate, etc.)
 * 
 * @author Marc Streit
 */
public class PartListener
	implements IPartListener2, IUniqueObject {

	IEventPublisher eventPublisher;

	public PartListener() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	@Override
	public int getID() {
		return 815; // FIXXXME unique id for this object or find another uniqueObject to trigger events from
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof ARcpGLViewPart))
			return;

		ARcpGLViewPart glView = (ARcpGLViewPart) activePart;

		GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(glView.getGLCanvas());
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof ARcpGLViewPart))
			return;

		ARcpGLViewPart glView = (ARcpGLViewPart) activePart;

		if (glView == null)
			return;

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return;

		// Remove view specific toolbar from general toolbar view
		RcpToolBarView toolBarView =
			(RcpToolBarView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
				RcpToolBarView.ID);

		if (toolBarView == null)
			return;

		toolBarView.removeViewSpecificToolBar(glView.getGLView().getID());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}
		CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;
		viewPart.setAttached(isViewAttached(viewPart));

		// GeneralManager.get().getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID,
		// "partVisible(): " +viewPart));

		if (viewPart instanceof ARcpGLViewPart) {
			ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;

			glViewPart.getGLView().setVisible(true);

		}

		if (!activePart.getSite().getShell().getText().equals("Caleydo")) {
			// viewpart is detached from caleydo main window
			drawInlineToolBar(viewPart);
			removeViewSpecificToolBarItems();
		}
		else {
			// viewpart is attached within caleydo main window
			removeInlineToolBar(viewPart);
			sendViewActivationEvent(viewPart);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// removeViewSpecificToolBarItems();
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}
		CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;

		// GeneralManager.get().getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID,
		// "partHidden(): " +viewPart));

		viewPart.setAttached(isViewAttached(viewPart));

		if (!(activePart instanceof ARcpGLViewPart)) {
			return;
		}
		ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;

		// GeneralManager.get().getViewGLCanvasManager()
		// .unregisterGLCanvasFromAnimator(glViewPart.getGLCanvas());
		glViewPart.getGLView().setVisible(false);

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);
		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}

		CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;

		// GeneralManager.get().getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID,
		// "partActivated(): " +viewPart));

		// Make sure that keyboard listener gets the events
		if (viewPart.getSWTComposite() != null)
			viewPart.getSWTComposite().forceFocus();

		sendViewActivationEvent(viewPart);
	}

	private void sendViewActivationEvent(CaleydoRCPViewPart viewPart) {
		ViewEvent viewActivationEvent;
		viewActivationEvent = new ViewActivationEvent();
		viewActivationEvent.setSender(this);
		viewActivationEvent.setViews(getAllViews(viewPart));
		
		// Do not trigger event when no view is assigned to view part (e.g. info area)
		if (viewActivationEvent.getViewIDs().get(0) == null)
			return;
		
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	/**
	 * Draws the toolbar items within the views default toolbar (inline)
	 * 
	 * @param viewPart
	 *            view to add the toolbar items
	 */
	private void drawInlineToolBar(CaleydoRCPViewPart viewPart) {
		List<IView> views = getAllViews(viewPart);

		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(views);

		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();

		toolBarManager.removeAll();
		for (AToolBarContent toolBarContent : toolBarContents) {
			for (ToolBarContainer container : toolBarContent.getInlineToolBar()) {
				for (IToolBarItem item : container.getToolBarItems()) {
					if (item instanceof IAction) {
						toolBarManager.add((IAction) item);
					}
					else if (item instanceof ControlContribution) {
						toolBarManager.add((ControlContribution) item);
					}
				}
				toolBarManager.add(new Separator());
			}
		}
		toolBarManager.update(true);
	}

	/**
	 * removes all view specific toolbar items in the toolbar view
	 */
	private void removeViewSpecificToolBarItems() {
		RemoveViewSpecificItemsEvent event;
		event = new RemoveViewSpecificItemsEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * removes all inline toolbar items
	 * 
	 * @param viewPart
	 *            view to remove the toolbar items from
	 */
	private void removeInlineToolBar(CaleydoRCPViewPart viewPart) {
		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		toolBarManager.removeAll();
		toolBarManager.update(true);
	}

	/**
	 * Gets the views and all sub-views (if there are any)
	 * 
	 * @param viewPart
	 * @return
	 */
	private List<IView> getAllViews(CaleydoRCPViewPart viewPart) {
		return viewPart.getAllViews();
	}

	public boolean isViewAttached(IViewPart viewPart) {
		if (viewPart.getSite().getShell().getText().equals("Caleydo")) {
			return true;
		}
		else {
			return false;
		}
	}
}
