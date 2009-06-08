package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that a request to cancel the currently running clusterer was triggered
 * 
 * @author Alexander Lex
 */
public class ClustererCanceledEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
