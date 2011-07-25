package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StartClusteringEvent;

public class StartClusteringListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof StartClusteringEvent) {
			StartClusteringEvent startClusteringEvent = (StartClusteringEvent) event;
			if (handler.getDataDomainID() == startClusteringEvent.getDataDomainID())
				handler.startClustering(startClusteringEvent.getDataTableID(),
					startClusteringEvent.getClusterState());
		}
	}

}
