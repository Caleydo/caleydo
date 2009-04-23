package org.caleydo.rcp.perspective;

import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.ViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.caleydo.rcp.views.opengl.AGLViewPart;
import org.caleydo.rcp.views.swt.toolbar.ToolBarContentFactory;
import org.caleydo.rcp.views.swt.toolbar.ToolBarView;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;
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
	implements IPartListener2, IUniqueObject, IMediatorSender {

	Logger log = Logger.getLogger(PartListener.class.getName());

	IEventPublisher eventPublisher; 

	public PartListener() {
		GeneralManager.get().getEventPublisher().addSender(EMediatorType.VIEW_SELECTION, this);
		eventPublisher = GeneralManager.get().getEventPublisher();
	}
	
	@Override
	public int getID() {
		return 815; // FIXXXME unique id for this object or find another uniqueObject to trigger events from
	}
	
	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		eventPublisher.triggerEvent(eMediatorType, this, eventContainer);
	}

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
		
		if (!(activePart instanceof CaleydoViewPart)) {
			return;
		}
		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;
		viewPart.setAttached(isViewAttached(viewPart));
		
		log.info("partVisible(): " +viewPart);

		if (viewPart instanceof AGLViewPart) {
			GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(
				((AGLViewPart) viewPart).getGLCanvas().getID());
		}
		
		if (!activePart.getSite().getShell().getText().equals("Caleydo")) {
			// viewpart is detached from caleydo main window
			drawInlineToolBar(viewPart);
			removeViewSpecificToolBarItems();
		} else {
			// viewpart is attached within caleydo main window
			removeInlineToolBar((CaleydoViewPart) viewPart);
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

		if (!(activePart instanceof CaleydoViewPart)) {
			return;
		}
		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;
		
		log.info("partHidden(): " +viewPart);
		
		viewPart.setAttached(isViewAttached(viewPart));

		if (!(activePart instanceof AGLViewPart)) {
			return;
		}
		AGLViewPart glViewPart = (AGLViewPart) activePart;

		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvasFromAnimator(
			glViewPart.getGLCanvas().getID());
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);
		if (!(activePart instanceof CaleydoViewPart)) {
			return;
		}
		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;
		
		log.info("partActivated(): " +viewPart);

		sendViewActivationEvent(viewPart);
	}

		
	private void sendViewActivationEvent(CaleydoViewPart viewPart) { 
		ViewEvent viewActivationEvent;
		viewActivationEvent = new ViewActivationEvent();			
		List<Integer> viewIDs = getAllViewIDs(viewPart);
		viewActivationEvent.setViewIDs(viewIDs);
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	/**
	 * draws the toolbar items within the views default toolbar (inline)
	 * @param viewPart view to add the toolbar items
	 */
	private void drawInlineToolBar(CaleydoViewPart viewPart) {
		List<Integer> viewIDs = getAllViewIDs(viewPart);
		
		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(viewIDs);

		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();

		toolBarManager.removeAll();
		for (AToolBarContent toolBarContent : toolBarContents) {
			for (ToolBarContainer container : toolBarContent.getInlineToolBar()) {
				for (IToolBarItem item : container.getToolBarItems()) {
					if (item instanceof IAction) {
						toolBarManager.add((IAction) item);
					} else if (item instanceof ControlContribution) {
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
		eventPublisher.triggerEvent(event);
	}
	
	/**
	 * removes all inline toolbar items
	 * @param viewPart view to remove the toolbar items from
	 */
	private void removeInlineToolBar(CaleydoViewPart viewPart) {
		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
		toolBarManager.removeAll();
		toolBarManager.update(true);
	}

	/**
	 * Gets the view-ids and all sub-view-ids (if there are any)
	 * @param viewPart
	 * @return
	 */
	private List<Integer> getAllViewIDs(CaleydoViewPart viewPart) {
		return viewPart.getAllViewIDs();
	}

//	List<Integer> viewIDs;
//		if (viewPart instanceof HTMLBrowserView) {
//			viewIDs = new ArrayList<Integer>();
//			viewIDs.add(viewPart.getViewID());
//		} else if (viewPart instanceof AGLViewPart) {
//			IViewManager viewManager = GeneralManager.get().getViewGLCanvasManager();
//			viewManager.setActiveSWTView(viewPart.getSWTComposite());
//			AGLEventListener glView = ((AGLViewPart) viewPart).getGLEventListener();
//			viewIDs = glView.getAllViewIDs();
//		} else {
//			viewIDs = new ArrayList<Integer>();
//		}
//		return viewIDs;
//	}
	
	public boolean isViewAttached(IViewPart viewPart) {
		if (viewPart.getSite().getShell().getText().equals("Caleydo")) {
			return true;
		} else {
			return false;
		}
	}
}
