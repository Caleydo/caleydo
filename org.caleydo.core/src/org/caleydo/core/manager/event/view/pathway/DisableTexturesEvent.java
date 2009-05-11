package org.caleydo.core.manager.event.view.pathway;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the pathway textures within 
 * pathway views should be disabled.
 * @author Werner Puff
 */
public class DisableTexturesEvent
	extends AEvent {
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
