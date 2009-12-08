package org.caleydo.core.manager.event.view;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to enable or disable magnifying glasses.
 * 
 * @author Christian Partl
 */
public class ToggleMagnifyingGlassEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
