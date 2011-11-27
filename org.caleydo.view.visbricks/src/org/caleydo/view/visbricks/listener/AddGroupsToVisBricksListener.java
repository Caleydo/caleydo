package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

/**
 * Listener for the event {@link AddGroupsToVisBricksEvent}.
 * 
 * @author Partl
 * 
 */
public class AddGroupsToVisBricksListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddGroupsToVisBricksEvent) {
			AddGroupsToVisBricksEvent addGroupsToVisBricksEvent = (AddGroupsToVisBricksEvent) event;
			if (addGroupsToVisBricksEvent.getReceiver() == handler) {
				handler.addDimensionGroups(addGroupsToVisBricksEvent
						.getDataContainers(), addGroupsToVisBricksEvent.getDataConfigurer());
			}
		}
	}
}
