package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.event.OpenViewEvent;

public class OpenViewEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof OpenViewEvent) {
			OpenViewEvent openViewEvent = (OpenViewEvent) event;
			handler.openView(openViewEvent.getView());
		}
	}

}
