package org.caleydo.core.view.swt.collab;

import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the collab view should be redrawn
 * 
 * @author Werner Puff
 */
public class RedrawCollabViewEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
