package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.grouper.CompareGroupsEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class CompareGroupsEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CompareGroupsEvent) {

			handler.setTablesToCompare(((CompareGroupsEvent) event).getTables());
		}
	}
}
