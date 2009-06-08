package org.caleydo.core.manager.event.view;

import org.caleydo.core.manager.event.AEvent;

/**
 * Clears all selections in a receiving view
 * 
 * @author Werner Puff
 */
public class ClearSelectionsEvent
	extends AEvent {
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
