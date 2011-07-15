package org.caleydo.view.datagraph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class DimensionGroupsChangedEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof DimensionGroupsChangedEvent) {
			DimensionGroupsChangedEvent dimensionGroupsChangedEvent = (DimensionGroupsChangedEvent)event;
			handler.updateDataDomain(dimensionGroupsChangedEvent.getDataDomain());
		}
	}

}
