package org.caleydo.view.grouper.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.event.DeleteGroupsEvent;

public class DeleteGroupsListener extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		DeleteGroupsEvent deleteGroupsEvent = null;
		if (event instanceof DeleteGroupsEvent) {
			deleteGroupsEvent = (DeleteGroupsEvent) event;
			handler.deleteGroups(deleteGroupsEvent.getGroupsToDelete());
		}
	}

}
