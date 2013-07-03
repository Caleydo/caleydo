/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.NewDataDomainLoadedEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.view.dvi.GLDataViewIntegrator;

public class DataDomainEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewDataDomainLoadedEvent) {
			NewDataDomainLoadedEvent newDataDomainEvent = (NewDataDomainLoadedEvent) event;
			handler.addDataDomain(newDataDomainEvent.getDataDomain());
		} else if (event instanceof RemoveDataDomainEvent) {
			handler.removeDataDomain(((RemoveDataDomainEvent) event).getEventSpace());
		}

	}

}
