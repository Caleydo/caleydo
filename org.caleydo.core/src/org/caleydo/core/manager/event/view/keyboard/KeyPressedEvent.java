package org.caleydo.core.manager.event.view.keyboard;

import org.caleydo.core.manager.event.AEvent;

public abstract class KeyPressedEvent
	extends AEvent {

	@Override
	public abstract boolean checkIntegrity();

}
