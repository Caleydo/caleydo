package org.caleydo.view.grouper.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.event.CreateGroupEvent;

public class CreateGroupListener extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		CreateGroupEvent createGroupEvent;
		if (event instanceof CreateGroupEvent) {
			createGroupEvent = (CreateGroupEvent) event;
			handler.createNewGroup(createGroupEvent.getContainedGroups());
		}

	}

}
