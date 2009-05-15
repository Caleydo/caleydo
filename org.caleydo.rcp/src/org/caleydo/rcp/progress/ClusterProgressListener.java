package org.caleydo.rcp.progress;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;

public class ClusterProgressListener extends AEventListener<ClusteringProgressBar> {

	@Override
	public void handleEvent(AEvent event) {
	if (event instanceof ClusterProgressEvent) {
		ClusterProgressEvent clusterProgressEvent = (ClusterProgressEvent) event;
		handler.setProgress(clusterProgressEvent.forSimilaritiesBar(), clusterProgressEvent.getPercentCompleted());
	}
		
	}

}
