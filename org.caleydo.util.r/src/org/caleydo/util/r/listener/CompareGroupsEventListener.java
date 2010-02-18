package org.caleydo.util.r.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class CompareGroupsEventListener extends AEventListener<RStatisticsPerformer> {

	@Override
	public void handleEvent(AEvent event) {
		CompareGroupsEvent compareGroupsEvent = null;
		if (event instanceof CompareGroupsEvent) {
			compareGroupsEvent = (CompareGroupsEvent) event;
			handler.compareSets(compareGroupsEvent.getSets());
		}
	}
}
