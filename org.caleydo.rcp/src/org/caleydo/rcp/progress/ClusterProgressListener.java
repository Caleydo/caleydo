package org.caleydo.rcp.progress;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;

public class ClusterProgressListener
	extends AEventListener<ClusteringProgressBar> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClusterProgressEvent) {
			ClusterProgressEvent clusterProgressEvent = (ClusterProgressEvent) event;
			handler.setProgress(clusterProgressEvent.isForSimilaritiesBar(), clusterProgressEvent
				.getPercentCompleted());
		}
		if (event instanceof RenameProgressBarEvent) {
			RenameProgressBarEvent renameProgressBarEvent = (RenameProgressBarEvent) event;
			handler.setProgressBarLabel(renameProgressBarEvent.getProgressbarTitle());
		}
	}
}
