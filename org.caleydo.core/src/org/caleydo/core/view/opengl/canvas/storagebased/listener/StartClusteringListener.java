package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.usecase.AUseCase;

public class StartClusteringListener
	extends AEventListener<AUseCase> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof StartClusteringEvent) {
			StartClusteringEvent startClusteringEvent = (StartClusteringEvent) event;

			handler.startClustering(startClusteringEvent.getClusterState());
		}
	}

}
