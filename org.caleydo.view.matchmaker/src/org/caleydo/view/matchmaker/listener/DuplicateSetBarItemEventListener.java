package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.matchmaker.DuplicateSetBarItemEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class DuplicateSetBarItemEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof DuplicateSetBarItemEvent) {
			DuplicateSetBarItemEvent duplicateSetBarItemEvent = (DuplicateSetBarItemEvent) event;
			handler.handleDuplicateSetBarItem(duplicateSetBarItemEvent.getItemID());
		}

	}

}
