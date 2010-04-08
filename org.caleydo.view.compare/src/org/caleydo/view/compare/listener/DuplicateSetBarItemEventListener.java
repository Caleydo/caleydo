package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.compare.DuplicateSetBarItemEvent;
import org.caleydo.view.compare.GLMatchmaker;

public class DuplicateSetBarItemEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		
		if(event instanceof DuplicateSetBarItemEvent) {
			DuplicateSetBarItemEvent duplicateSetBarItemEvent = (DuplicateSetBarItemEvent) event;
			handler.handleDuplicateSetBarItem(duplicateSetBarItemEvent.getItemID());
		}
		
	}

}
