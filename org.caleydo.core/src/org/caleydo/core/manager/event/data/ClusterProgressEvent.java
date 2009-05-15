package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;

public class ClusterProgressEvent
	extends AEvent {

	private int percentCompleted = -1;
	private boolean forSimilaritiesBar;

	public ClusterProgressEvent(int percentCompleted, boolean forSimilaritiesBar) {
		this.percentCompleted = percentCompleted;
		this.forSimilaritiesBar = forSimilaritiesBar;
	}

	public boolean forSimilaritiesBar() {
		return forSimilaritiesBar;
	}

	public int getPercentCompleted() {
		return percentCompleted;
	}

	@Override
	public boolean checkIntegrity() {
		if (percentCompleted < 0 || percentCompleted > 100)
			throw new IllegalStateException("percentCompleted was outside of the valid range (0 - 100): "
				+ percentCompleted);

		return true;
	}

}
