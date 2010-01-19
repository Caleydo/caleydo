package org.caleydo.view.grouper.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.CopyGroupsEvent;
import org.caleydo.view.grouper.GLGrouper;

public class CopyGroupsEventListener extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		CopyGroupsEvent copyGroupsEvent = null;
		if (event instanceof CopyGroupsEvent) {
			copyGroupsEvent = (CopyGroupsEvent) event;
			handler.copyGroups(copyGroupsEvent.getGroupsToCopy());
		}

	}

}
