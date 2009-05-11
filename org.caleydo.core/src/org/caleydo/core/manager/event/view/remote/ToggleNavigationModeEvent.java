package org.caleydo.core.manager.event.view.remote;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the navigation mode should be toggled.
 * 
 * @author Marc Streit
 */
public class ToggleNavigationModeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
