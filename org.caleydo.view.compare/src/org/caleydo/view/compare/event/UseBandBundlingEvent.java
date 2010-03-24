package org.caleydo.view.compare.event;

import org.caleydo.core.manager.event.AEvent;

/**
 * @author Marc Streit
 * 
 */
public class UseBandBundlingEvent extends AEvent {

	private boolean useBandBundling;

	public UseBandBundlingEvent() {
	}

	public UseBandBundlingEvent(boolean useBandBundling) {
		this.useBandBundling = useBandBundling;
	}

	public void setUseSorting(boolean useBandBundling) {
		this.useBandBundling = useBandBundling;
	}

	public boolean isBandBundlingActive() {
		return useBandBundling;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
