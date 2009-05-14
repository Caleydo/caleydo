package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AFlagSetterEvent;

public class UseRandomSamplingEvent
	extends AFlagSetterEvent {

	public UseRandomSamplingEvent(boolean flag) {
		super(flag);
	}
}
