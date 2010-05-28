package org.caleydo.view.bucket.toolbar;

import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.remote.DisableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.EnableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.ResetRemoteRendererEvent;
import org.caleydo.core.manager.event.view.remote.ToggleNavigationModeEvent;
import org.caleydo.core.manager.event.view.remote.ToggleZoomEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.listener.DisableConnectionLinesListener;
import org.caleydo.rcp.view.listener.EnableConnectionLinesListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.eclipse.swt.widgets.Display;

/**
 * Mediator for remote-rendering (bucket) related toolbar items
 * 
 * @author Werner Puff
 */
public class RemoteRenderingToolBarMediator implements IRemoteRenderingHandler {

	private IEventPublisher eventPublisher;

	/**
	 * related toolBarContent that contains the gui-control items for
	 * mediatation
	 */
	private RemoteRenderingToolBarContent toolBarContent;

	protected EnableConnectionLinesListener enableConnectionLinesListener;
	protected DisableConnectionLinesListener disableConnectionLinesListener;

	public RemoteRenderingToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void registerEventListeners() {
		enableConnectionLinesListener = new EnableConnectionLinesListener();
		enableConnectionLinesListener.setHandler(this);
		eventPublisher.addListener(EnableConnectionLinesEvent.class,
				enableConnectionLinesListener);

		disableConnectionLinesListener = new DisableConnectionLinesListener();
		disableConnectionLinesListener.setHandler(this);
		eventPublisher.addListener(DisableConnectionLinesEvent.class,
				disableConnectionLinesListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (enableConnectionLinesListener != null) {
			eventPublisher.removeListener(enableConnectionLinesListener);
			enableConnectionLinesListener = null;
		}
		if (disableConnectionLinesListener != null) {
			eventPublisher.removeListener(disableConnectionLinesListener);
			disableConnectionLinesListener = null;
		}

	}

	public void enableConnectionLines() {
		EnableConnectionLinesEvent event = new EnableConnectionLinesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableConnectionLines() {
		DisableConnectionLinesEvent event = new DisableConnectionLinesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void toggleNavigationMode() {
		ToggleNavigationModeEvent event = new ToggleNavigationModeEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void closeOrResetViews() {
		ResetRemoteRendererEvent event = new ResetRemoteRendererEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void toggleZoom() {
		ToggleZoomEvent event = new ToggleZoomEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void queueEvent(
			final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		System.out.println("queue: listener.handleEvent(event);");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
				System.out.println("listener.handleEvent(event);");
			}
		});
	}

	/**
	 * Releases all obtained resources (e.g. event-listeners.
	 */
	public void dispose() {
		unregisterEventListeners();
	}

	/**
	 * Gets the related {@link RemoteRenderingToolBarContent} of this mediator
	 * 
	 * @return {@link RemoteRenderingToolBarContent} that is mediated
	 */
	public RemoteRenderingToolBarContent getToolBarContent() {
		return toolBarContent;
	}

	/**
	 * Sets the related {@link RemoteRenderingToolBarContent} for this mediator
	 * 
	 * @param toolBarContent
	 *            {@link RemoteRenderingToolBarContent} to mediate
	 */
	public void setToolBarContent(RemoteRenderingToolBarContent toolBarContent) {
		this.toolBarContent = toolBarContent;
	}

	@Override
	public void setConnectionLinesEnabled(boolean enabled) {
		toolBarContent.toggleConnectionLinesAction
				.setConnectionLinesEnabled(enabled);
	}

	@Override
	public void addPathwayView(int pathwayID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

}
