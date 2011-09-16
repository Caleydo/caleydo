package org.caleydo.view.treemap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.treemap.ToggleLabelEvent;
import org.caleydo.view.treemap.GLTreeMap;

/**
 * Listener for switch label on/off.
 * @author Michael Lafer
 *
 */

public class ToggleLabelListener extends AEventListener<GLTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		ToggleLabelEvent tlevent= (ToggleLabelEvent) event;
		handler.setDrawLabel(tlevent.isDrawLabel());
	
	}

}