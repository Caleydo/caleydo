package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.view.datagraph.GLDataViewIntegrator;

public class DimensionGroupsChangedEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof DataDomainUpdateEvent) {
			DataDomainUpdateEvent dimensionGroupsChangedEvent = (DataDomainUpdateEvent) event;
			handler.updateDataDomain(dimensionGroupsChangedEvent.getDataDomain());
		}
	}

}
