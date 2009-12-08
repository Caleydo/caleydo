package org.caleydo.core.manager.event.view.hyperbolic;

import org.caleydo.core.manager.event.AEvent;

public class ChangeCanvasDrawingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}