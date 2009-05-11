package org.caleydo.core.manager.event.view.pathway;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the neighborhood visualization within 
 * pathway views should be enabled.
 * @author Werner Puff
 */
public class EnableNeighborhoodEvent
	extends AEvent {
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
