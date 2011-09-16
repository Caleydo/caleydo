package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.matchmaker.AdjustPValueEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class AdjustPValueOfSetEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof AdjustPValueEvent) {
			handler.handleAdjustPValue();
		}
	}
}
