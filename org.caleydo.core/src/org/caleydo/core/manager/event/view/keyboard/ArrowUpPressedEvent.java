package org.caleydo.core.manager.event.view.keyboard;


/**
 * This event signals that arrowUp button was pressed
 * 
 * @author Bernhard Schlegl
 */
public class ArrowUpPressedEvent
	extends KeyPressedEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
