package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.matchmaker.DuplicateTableBarItemEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class DuplicateTableBarItemEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof DuplicateTableBarItemEvent) {
			DuplicateTableBarItemEvent duplicateSetBarItemEvent = (DuplicateTableBarItemEvent) event;
			handler.handleDuplicateSetBarItem(duplicateSetBarItemEvent.getItemID());
		}
	}
}
