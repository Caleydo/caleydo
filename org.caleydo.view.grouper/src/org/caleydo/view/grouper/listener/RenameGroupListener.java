package org.caleydo.view.grouper.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.event.RenameGroupEvent;

public class RenameGroupListener extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		RenameGroupEvent copyGroupsEvent = null;
		if (event instanceof RenameGroupEvent) {
			copyGroupsEvent = (RenameGroupEvent) event;
			handler.renameGroup(copyGroupsEvent.getGroupID());
		}
	}
}
