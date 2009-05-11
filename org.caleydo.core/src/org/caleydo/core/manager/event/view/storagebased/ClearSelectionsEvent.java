package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * TODO description Migration from EEventType.VIEW_COMMAND and EViewCommand.CLEAR_SELECTIONS
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
