package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.AttachedViewActivationEvent;
import org.caleydo.core.manager.event.view.DetachedViewActivationEvent;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.listener.DetachedViewActivationListener;
import org.caleydo.rcp.views.swt.toolbar.listener.RemoveViewSpecificItemsEventListener;
import org.caleydo.rcp.views.swt.toolbar.listener.ViewActivationListener;
import org.eclipse.swt.widgets.Display;

/**
 * Event handler to change toolbar according to gloabl caleydo-events. 
 * For example to change to displays toolbar in dependency of the active view. 
 * @author Werner Puff
 */
public class ToolBarMediator {

	public static Logger log = Logger.getLogger(ToolBarMediator.class.getName());

	/** the related toolbar that should react to events */
	ToolBarView toolBarView;

	ViewActivationListener viewActivationListener;
	DetachedViewActivationListener detachedViewActivationListener;
	RemoveViewSpecificItemsEventListener removeViewSpecificItemsEventListener;
	
	public ToolBarMediator() {
		registerEventListeners();
	}

	/**
	 * Renders the toolbar with the related content to the given views
	 * @param viewIDs list of viewIDs to render a toolbar for
	 */
	public void renderToolBar(List<Integer> viewIDs) {
		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(viewIDs);
		
		IToolBarRenderer renderer = toolBarView.getToolBarRenderer();
		Runnable job = renderer.createRenderJob(toolBarView, toolBarContents);
		Display display = toolBarView.getParentComposite().getDisplay(); 
		display.asyncExec(job);
	}

	/**
	 * 
	 * @return
	 */
	public void removeViewSpecificToolBar() {
		
	}
	
	public ToolBarView getToolBarView() {
		return toolBarView;
	}

	public void setToolBarView(ToolBarView toolBarView) {
		this.toolBarView = toolBarView;
	}

	public void registerEventListeners() {
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		
		viewActivationListener = new ViewActivationListener();
		viewActivationListener.setToolBarMediator(this);
		eventPublisher.addListener(AttachedViewActivationEvent.class, viewActivationListener);

		detachedViewActivationListener = new DetachedViewActivationListener();
		detachedViewActivationListener.setToolBarMediator(this);
		eventPublisher.addListener(DetachedViewActivationEvent.class, detachedViewActivationListener);
		
		removeViewSpecificItemsEventListener = new RemoveViewSpecificItemsEventListener();
		removeViewSpecificItemsEventListener.setToolBarMediator(this);
		eventPublisher.addListener(RemoveViewSpecificItemsEvent.class, removeViewSpecificItemsEventListener);
}
	
	public void unregisterEventListeners() {
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

		if (viewActivationListener != null) {
			eventPublisher.removeListener(AttachedViewActivationEvent.class, viewActivationListener);
			viewActivationListener = null;
		}
		if (detachedViewActivationListener != null) {
			eventPublisher.removeListener(DetachedViewActivationEvent.class, detachedViewActivationListener);
			detachedViewActivationListener = null;
		}
		if (removeViewSpecificItemsEventListener != null) {
			eventPublisher.removeListener(RemoveViewSpecificItemsEvent.class, detachedViewActivationListener);
			removeViewSpecificItemsEventListener = null;
		}
	}
	
}
