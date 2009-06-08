package org.caleydo.core.manager.event.view.pathway;

import org.caleydo.core.manager.event.AEvent;

/**
 * Events that signals that gene mapping within 
 * pathway views should be enabled.
 * @author Werner Puff
 */
public class EnableGeneMappingEvent
	extends AEvent {
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
