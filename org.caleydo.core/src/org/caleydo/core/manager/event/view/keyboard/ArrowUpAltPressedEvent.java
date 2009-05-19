package org.caleydo.core.manager.event.view.keyboard;


/**
 * This event signals that arrowUp button while holding alt button was pressed
 * 
 * @author Bernhard Schlegl
 */
public class ArrowUpAltPressedEvent
	extends KeyPressedEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
