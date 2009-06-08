package org.caleydo.core.manager.event.view;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that all view specific toolbar-items should be removed.
 * @author Werner Puff
 */
public class RemoveViewSpecificItemsEvent
	extends AEvent {
	
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
