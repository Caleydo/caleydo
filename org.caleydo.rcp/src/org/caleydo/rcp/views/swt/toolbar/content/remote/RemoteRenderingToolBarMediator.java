package org.caleydo.rcp.views.swt.toolbar.content.remote;

import java.util.logging.Logger;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.remote.CloseOrResetViewsEvent;
import org.caleydo.core.manager.event.view.remote.DisableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.EnableConnectionLinesEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.views.swt.toolbar.content.pathway.PathwayToolBarMediator;

/**
 * mediator for remote-rendering (bucket) related toolbar items
 * @author Werner Puff
 */
public class RemoteRenderingToolBarMediator {

	Logger log = Logger.getLogger(PathwayToolBarMediator.class.getName());
	
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

	public void closeOrResetViews() {
		CloseOrResetViewsEvent event = new CloseOrResetViewsEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
}
