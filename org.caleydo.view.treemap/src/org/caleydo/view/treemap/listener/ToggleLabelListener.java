package org.caleydo.view.treemap.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.treemap.ToggleLabelEvent;
import org.caleydo.view.treemap.GLTreeMap;

public class ToggleLabelListener extends AEventListener<GLTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		ToggleLabelEvent tlevent= (ToggleLabelEvent) event;
		handler.setDrawLabel(tlevent.isDrawLabel());
	
	}

}