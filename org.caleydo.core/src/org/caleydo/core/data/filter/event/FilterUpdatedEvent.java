package org.caleydo.core.data.filter.event;

import org.caleydo.core.manager.event.AEvent;

public class FilterUpdatedEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
