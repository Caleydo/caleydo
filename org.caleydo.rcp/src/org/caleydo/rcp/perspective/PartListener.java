package org.caleydo.rcp.perspective;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.EViewCommand;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.ViewActivationCommandEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.caleydo.rcp.views.opengl.AGLViewPart;
import org.caleydo.rcp.views.swt.HTMLBrowserView;
import org.caleydo.rcp.views.swt.toolbar.ToolBarContentFactory;
import org.caleydo.rcp.views.swt.toolbar.ToolBarView;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ActionToolBarContainer;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
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
	implements IPartListener2, IUniqueObject, IMediatorSender {

	Logger log = Logger.getLogger(PartListener.class.getName());
	
	public PartListener() {
		GeneralManager.get().getEventPublisher().addSender(EMediatorType.VIEW_SELECTION, this);
	}
	
	@Override
	public int getID() {
		return 815; // FIXXXME unique id for this object or find another uniqueObject to trigger events from
	}
	
	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		GeneralManager.get().getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
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
		log.info("partVisible() started");

		IWorkbenchPart activePart = partRef.getPart(false);
		
		if (!(activePart instanceof CaleydoViewPart)) {
			return;
		}

		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;

		if (viewPart instanceof AGLViewPart) {
			GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(
				((AGLViewPart) viewPart).getGLCanvas().getID());
		}
		
		if (!activePart.getSite().getShell().getText().equals("Caleydo")) {
			// viewpart is detached

			if (viewPart instanceof AGLViewPart) {
				drawInlineToolBar((AGLViewPart) viewPart);
			}
		}
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

		IWorkbenchPart activePart = partRef.getPart(false);
		if (!(activePart instanceof CaleydoViewPart)) {
			return;
		}
		CaleydoViewPart viewPart = (CaleydoViewPart) activePart;

		ViewActivationCommandEventContainer viewActivationEvent;
		viewActivationEvent = new ViewActivationCommandEventContainer(EViewCommand.ACTIVATION);

		if (viewPart instanceof HTMLBrowserView) {
			List<Integer> viewIDs = new ArrayList<Integer>();
			viewIDs.add(viewPart.getViewID());
			viewActivationEvent.setViewIDs(viewIDs);
		} else {
			GeneralManager.get().getViewGLCanvasManager().setActiveSWTView(
				((CaleydoViewPart) activePart).getSWTComposite());

			AGLEventListener glView = ((AGLViewPart) viewPart).getGLEventListener();
	
			List<Integer> viewIDs = glView.getAllViewIDs();
			viewActivationEvent.setViewIDs(viewIDs);
		}

		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher(); 
		eventPublisher.triggerEvent(EMediatorType.VIEW_SELECTION, this, viewActivationEvent);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	private void drawInlineToolBar(AGLViewPart viewPart) {
		AGLViewPart glViewPart = (AGLViewPart) viewPart;
		AGLEventListener glView = glViewPart.getGLEventListener();
		List<Integer> viewIDs = glView.getAllViewIDs();
		
		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(viewIDs);

		final IToolBarManager toolBarManager = viewPart.getViewSite().getActionBars().getToolBarManager();

		toolBarManager.removeAll();
		for (AToolBarContent toolBarContent : toolBarContents) {
			for (ToolBarContainer container : toolBarContent.getDefaultToolBar()) {
				for (IAction toolBarAction : ((ActionToolBarContainer) container).getActions()) {
					toolBarManager.add(toolBarAction);
				}
				toolBarManager.add(new Separator());
			}
		}
		toolBarManager.update(true);
	}
}
