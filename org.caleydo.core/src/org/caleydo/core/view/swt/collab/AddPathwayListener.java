package org.caleydo.core.view.swt.collab;

import java.util.logging.Logger;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.view.pathway.LoadPathwayEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class AddPathwayListener 
	implements Listener, IMediatorSender {
	
	Logger log = Logger.getLogger(AddPathwayListener.class.getName());
	
	Object requester;

	@Override
	public void handleEvent(Event e) {
		LoadPathwayEvent loadEvent = new LoadPathwayEvent();
		loadEvent.setPathwayID(49558);
		IEventPublisher ep = GeneralManager.get().getEventPublisher();
		ep.triggerEvent(loadEvent);
	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

	@Override
	public void triggerEvent(EMediatorType mediatorType, IEventContainer eventContainer) {
	}

}
