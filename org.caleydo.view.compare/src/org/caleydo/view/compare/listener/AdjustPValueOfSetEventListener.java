package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.compare.AdjustPValueEvent;
import org.caleydo.view.compare.GLCompare;

public class AdjustPValueOfSetEventListener extends AEventListener<GLCompare> {

	@Override
	public void handleEvent(AEvent event) {
		
		if(event instanceof AdjustPValueEvent) {
			handler.handleAdjustPValue();
		}
	}
}
