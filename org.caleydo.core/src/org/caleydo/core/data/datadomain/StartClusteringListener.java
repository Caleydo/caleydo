package org.caleydo.core.data.datadomain;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StartClusteringEvent;

public class StartClusteringListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof StartClusteringEvent) {
			StartClusteringEvent startClusteringEvent = (StartClusteringEvent) event;
			if (handler.getDataDomainID() == startClusteringEvent.getDataDomainID())
				handler.startClustering(startClusteringEvent.getClusteConfiguration());
		}
	}

}
