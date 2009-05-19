package org.caleydo.core.manager.event.view.keyboard;


/**
 * This event signals that arrowRight button was pressed
 * 
 * @author Bernhard Schlegl
 */
public class ArrowRightPressedEvent
	extends KeyPressedEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
