package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals, that new meta sets where created.
 * 
 * @author Alexander lex
 */
public class NewMetaSetsEvent
	extends AEvent {
	
	

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
