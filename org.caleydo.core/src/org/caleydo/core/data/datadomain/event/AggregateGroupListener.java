/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
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
