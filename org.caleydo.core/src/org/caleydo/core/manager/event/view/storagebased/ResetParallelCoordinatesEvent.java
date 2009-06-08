package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the parallel coordinates should be reset
 * 
 * @author Alexander Lex
 */
public class ResetParallelCoordinatesEvent
	extends AEvent {
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
