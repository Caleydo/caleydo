package org.caleydo.view.stratomex20.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.stratomex20.GLStratomex20;

/**
 * Listener for the event {@link AddGroupsToStratomexEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class AddGroupsToStratomexListener extends AEventListener<GLStratomex20> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddGroupsToStratomexEvent) {
			AddGroupsToStratomexEvent addGroupsToStratomexEvent = (AddGroupsToStratomexEvent) event;
			if (addGroupsToStratomexEvent.getReceiver() == handler) {
				handler.addDimensionGroups(addGroupsToStratomexEvent.getTablePerspectives(),
						addGroupsToStratomexEvent.getDataConfigurer());
			}
		}
		
		if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler) {
				handler.addTablePerspectives(addTablePerspectivesEvent.getTablePerspectives(),
						null);
			}
		}
	}
}
