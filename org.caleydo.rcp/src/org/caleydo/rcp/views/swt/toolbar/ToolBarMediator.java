package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.listener.GroupHighlightingListener;
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

	protected ViewActivationListener viewActivationListener;
	protected RemoveViewSpecificItemsEventListener removeViewSpecificItemsEventListener;
	protected GroupHighlightingListener groupHighlightingListener;
	
	public ToolBarMediator() {
		registerEventListeners();
	}

	/**
	 * Renders the toolbar with the related content to the given views
	 * @param viewIDs list of viewIDs to render a toolbar for
	 */
	public void renderToolBar(List<Integer> viewIDs) {
		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		boolean isIgnored = contentFactory.isIgnored(viewIDs);
		if (!isIgnored) {
			List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(viewIDs);
			
			IToolBarRenderer renderer = toolBarView.getToolBarRenderer();
			Runnable job = renderer.createRenderJob(toolBarView, toolBarContents);
			Display display = toolBarView.getParentComposite().getDisplay(); 
			display.asyncExec(job);
		}
	}

	/**
	 * Highlight a toolbar-group related to the trigger of the event
	 * @param eventTrigger to highlight a related toolbar-group of
	 */
	public void highlightViewSpecificGroup(final Object eventTrigger) {
		toolBarView.highlightViewSpecificGroup(eventTrigger);
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
		viewActivationListener.setHandler(this);
		eventPublisher.addListener(ViewActivationEvent.class, viewActivationListener);
		
		removeViewSpecificItemsEventListener = new RemoveViewSpecificItemsEventListener();
		removeViewSpecificItemsEventListener.setHandler(this);
		eventPublisher.addListener(RemoveViewSpecificItemsEvent.class, removeViewSpecificItemsEventListener);
		
		groupHighlightingListener = new GroupHighlightingListener();
		groupHighlightingListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, groupHighlightingListener);
		eventPublisher.addListener(SelectionUpdateEvent.class, groupHighlightingListener);
}
	
	public void unregisterEventListeners() {
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

		if (viewActivationListener != null) {
			eventPublisher.removeListener(ViewActivationEvent.class, viewActivationListener);
			viewActivationListener = null;
		}
		if (removeViewSpecificItemsEventListener != null) {
			eventPublisher.removeListener(RemoveViewSpecificItemsEvent.class, removeViewSpecificItemsEventListener);
			removeViewSpecificItemsEventListener = null;
		}
		if (groupHighlightingListener != null) {
			eventPublisher.removeListener(groupHighlightingListener);
			groupHighlightingListener = null;
		}
	}
	
}
