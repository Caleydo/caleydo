package org.caleydo.view.visbricks.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupsToVisBricksListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof AddGroupsToVisBricksEvent) {
			AddGroupsToVisBricksEvent addGroupsToVisBricksEvent = (AddGroupsToVisBricksEvent)event;
			handler.addDimensionGroups(addGroupsToVisBricksEvent.getDimensionGroupData());
		}
		
	}

}
