package org.caleydo.core.manager.event.view.grouper;

import org.caleydo.core.manager.event.AEvent;

public class CreateGroupEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
