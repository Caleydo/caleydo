package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class NewViewEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewViewEvent) {
			NewViewEvent newViewEvent = (NewViewEvent) event;
			handler.addView(newViewEvent.getView());
		}

	}

}
