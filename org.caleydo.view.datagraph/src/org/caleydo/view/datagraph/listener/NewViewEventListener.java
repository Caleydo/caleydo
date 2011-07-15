package org.caleydo.view.datagraph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.NewViewEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class NewViewEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof NewViewEvent) {
			NewViewEvent newViewEvent = (NewViewEvent)event;
			handler.addView(newViewEvent.getView());
		}
		
	}

}
