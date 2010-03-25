package org.caleydo.view.grouper.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.event.PasteGroupsEvent;

public class PasteGroupsListener extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		PasteGroupsEvent pasteGroupsEvent = null;
		if (event instanceof PasteGroupsEvent) {
			pasteGroupsEvent = (PasteGroupsEvent) event;
			handler.pasteGroups(pasteGroupsEvent.getParentGroupID());
		}

	}

}
