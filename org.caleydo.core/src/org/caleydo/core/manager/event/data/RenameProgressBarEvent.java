package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;

public class RenameProgressBarEvent
	extends AEvent {

	private String stProgressbarTitle = null;

	public RenameProgressBarEvent(String stProgressBarTitle) {
		this.stProgressbarTitle = stProgressBarTitle;
	}

	public String getProgressbarTitle() {
		return stProgressbarTitle;
	}

	@Override
	public boolean checkIntegrity() {
		if (stProgressbarTitle == null)
			return false;
		return true;
	}
}
