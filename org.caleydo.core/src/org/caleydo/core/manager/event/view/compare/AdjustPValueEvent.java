package org.caleydo.core.manager.event.view.compare;

import org.caleydo.core.manager.event.AEvent;

public class AdjustPValueEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
