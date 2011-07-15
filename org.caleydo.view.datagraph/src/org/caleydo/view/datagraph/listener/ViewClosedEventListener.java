package org.caleydo.view.datagraph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.ViewClosedEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class ViewClosedEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof ViewClosedEvent) {
			ViewClosedEvent viewClosedEvent = (ViewClosedEvent)event;
			handler.removeView(viewClosedEvent.getView());
		}
	}

}
