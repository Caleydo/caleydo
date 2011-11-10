package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.DataDomainsChangedEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class DataDomainsChangedEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof DataDomainsChangedEvent) {
			DataDomainsChangedEvent dataDomainsChangedEvent = (DataDomainsChangedEvent) event;
			handler.updateView(dataDomainsChangedEvent.getView());
		}
	}

}
