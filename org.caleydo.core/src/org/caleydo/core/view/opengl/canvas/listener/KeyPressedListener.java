package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.keyboard.ArrowDownAltPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowDownCtrlPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowDownPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowLeftPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowRightPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpAltPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpCtrlPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpPressedEvent;

/**
 * Listener for the key pressed events
 * 
 * @author Bernhard Schlegl
 */
public class KeyPressedListener
	extends AEventListener<IKeyPressedHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ArrowDownPressedEvent) {
			handler.handleArrowDownPressed();
		}
		else if (event instanceof ArrowDownAltPressedEvent) {
			handler.handleArrowDownAltPressed();
		}
		else if (event instanceof ArrowDownCtrlPressedEvent) {
			handler.handleArrowDownCtrlPressed();
		}
		else if (event instanceof ArrowUpPressedEvent) {
			handler.handleArrowUpPressed();
		}
		else if (event instanceof ArrowUpCtrlPressedEvent) {
			handler.handleArrowUpCtrlPressed();
		}
		else if (event instanceof ArrowUpAltPressedEvent) {
			handler.handleArrowUpAltPressed();
		}
		else if (event instanceof ArrowLeftPressedEvent) {
			handler.handleArrowLeftPressed();
		}
		else if (event instanceof ArrowRightPressedEvent) {
			handler.handleArrowRightPressed();
		}
	}
}
