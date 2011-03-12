package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;

public class RelationsUpdatedEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
