package org.caleydo.core.event.view.matchmaker;

import org.caleydo.core.event.AEvent;

public class AdjustPValueEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
