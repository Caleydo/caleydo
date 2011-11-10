package org.caleydo.view.treemap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.treemap.ToggleColoringModeEvent;
import org.caleydo.view.treemap.GLTreeMap;

/**
 * Listener for toggling coloring mode.
 * 
 * @author Michael Lafer
 * 
 */

public class ToggleColoringModeListener extends AEventListener<GLTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		ToggleColoringModeEvent cmevent = (ToggleColoringModeEvent) event;
		handler.setCalculateColor(cmevent.isCalculateColor());
	}

}