package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that angular brushing should be activated
 * 
 * @author Alexander Lex
 */
public class ActivateAngularBrushingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
