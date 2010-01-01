package org.caleydo.core.view.opengl.canvas.grouper.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.CreateGroupEvent;
import org.caleydo.core.view.opengl.canvas.grouper.GLGrouper;

public class CreateGroupEventListener
	extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		CreateGroupEvent createGroupEvent;
		if (event instanceof CreateGroupEvent) {
			createGroupEvent = (CreateGroupEvent) event;
			handler.createNewGroup(createGroupEvent.getContainedGroups());
		}

	}

}
