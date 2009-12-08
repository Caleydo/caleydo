package org.caleydo.core.manager.event.view.keyboard;

import org.caleydo.core.manager.event.AEvent;
import org.eclipse.swt.events.KeyEvent;

/**
 * Wrapper for SWT key event to make the events thread-safe.
 * 
 * @author Alexander Lex
 */
public class WrapperKeyEvent
	extends AEvent {

	KeyEvent event;

	public WrapperKeyEvent(KeyEvent event) {
		this.event = event;
	}

	/**
	 * Returns the SWT key event
	 * 
	 * @return the key event
	 */
	public KeyEvent getKeyEvent() {
		return event;
	}

	@Override
	public boolean checkIntegrity() {
		if (event == null)
			return false;
		return true;
	}

}
