package org.caleydo.core.manager.event.view.keyboard;


/**
 * This event signals that arrowLeft button was pressed
 * 
 * @author Bernhard Schlegl
 */
public class ArrowLeftPressedEvent
	extends KeyPressedEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
