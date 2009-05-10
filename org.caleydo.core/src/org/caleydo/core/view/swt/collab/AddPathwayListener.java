package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class AddPathwayListener 
	implements Listener  {
		
	Object requester;

	@Override
	public void handleEvent(Event e) {
		LoadPathwayEvent loadEvent = new LoadPathwayEvent();
		loadEvent.setSender(this);
		loadEvent.setPathwayID(49558);
		IEventPublisher ep = GeneralManager.get().getEventPublisher();
		ep.triggerEvent(loadEvent);
	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
