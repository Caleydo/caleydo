package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the spacing between the axis should be reset.
 * 
 * @author Alexander Lex
 */
public class ResetAxisSpacingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
