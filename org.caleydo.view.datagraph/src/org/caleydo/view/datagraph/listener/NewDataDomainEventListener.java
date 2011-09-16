package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class NewDataDomainEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof NewDataDomainEvent) {
			NewDataDomainEvent newDataDomainEvent = (NewDataDomainEvent)event;
			handler.addDataDomain(newDataDomainEvent.getDataDomain());
		}
	}

}
