package org.caleydo.core.manager.usecase;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StartClusteringEvent;

public class StartClusteringListener
	extends AEventListener<AUseCase> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof StartClusteringEvent) {
			StartClusteringEvent startClusteringEvent = (StartClusteringEvent) event;

			handler.startClustering(startClusteringEvent.getSetID(), startClusteringEvent.getClusterState());
		}
	}

}
