package org.caleydo.core.manager.event.view;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that all views should be reset
 * @author Werner Puff
 */
public class ResetAllViewsEvent
	extends AEvent {
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
