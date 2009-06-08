package org.caleydo.core.manager.event.view.remote;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal a zoom event in the bucket.
 * 
 * @author Marc Streit
 */
public class ToggleZoomEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
