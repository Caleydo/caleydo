package org.caleydo.rcp.view.swt.toolbar.content.remote;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.core.manager.event.view.remote.DisableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.EnableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.ResetRemoteRendererEvent;
import org.caleydo.core.manager.event.view.remote.ToggleNavigationModeEvent;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * mediator for remote-rendering (bucket) related toolbar items
 * 
 * @author Werner Puff
 */
public class RemoteRenderingToolBarMediator {

	IEventPublisher eventPublisher;

	public RemoteRenderingToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
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
}
