package org.caleydo.core.data.filter.event;

import org.caleydo.core.manager.event.AEvent;

/**
 * Signal that some property of the filters requiring adaption of a visualization of the filters was changed
 * 
 * @author Alexander Lex
 */
public class FilterUpdatedEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
