package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals that the one step back shall be taken in the navigation history of RadialHierarchy.
 * 
 * @author Christian Partl
 */
public class GoBackInHistoryEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
