package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

public class GoBackInHistoryEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
