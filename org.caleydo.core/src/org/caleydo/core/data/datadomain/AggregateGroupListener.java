package org.caleydo.core.data.datadomain;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * Listener for {@link AggregateGroupEvent}s
 * 
 * @author Alexander Lex
 */
public class AggregateGroupListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AggregateGroupEvent) {
			AggregateGroupEvent aggregateGroupEvent = (AggregateGroupEvent) event;
			handler.aggregateGroups(aggregateGroupEvent.getGroups());
		}
	}

}
