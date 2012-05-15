package org.caleydo.view.visbricks20.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.stratomex.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks20.GLVisBricks20;

/**
 * Listener for the event {@link AddGroupsToVisBricksEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class AddGroupsToVisBricksListener extends AEventListener<GLVisBricks20> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddGroupsToVisBricksEvent) {
			AddGroupsToVisBricksEvent addGroupsToVisBricksEvent = (AddGroupsToVisBricksEvent) event;
			if (addGroupsToVisBricksEvent.getReceiver() == handler) {
				handler.addDimensionGroups(addGroupsToVisBricksEvent.getDataContainers(),
						addGroupsToVisBricksEvent.getDataConfigurer());
			}
		}
	}
}
