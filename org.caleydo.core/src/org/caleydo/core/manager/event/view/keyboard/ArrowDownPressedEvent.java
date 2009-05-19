package org.caleydo.core.manager.event.view.keyboard;


/**
 * This event signals that arrowDown button was pressed
 * 
 * @author Bernhard Schlegl
 */
public class ArrowDownPressedEvent
	extends KeyPressedEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
