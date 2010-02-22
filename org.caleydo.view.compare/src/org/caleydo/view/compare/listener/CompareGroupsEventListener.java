package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.view.compare.GLCompare;

public class CompareGroupsEventListener extends AEventListener<GLCompare> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CompareGroupsEvent) {
			handler.setGroupsToCompare(((CompareGroupsEvent) event).getSets());
		}

	}

}
