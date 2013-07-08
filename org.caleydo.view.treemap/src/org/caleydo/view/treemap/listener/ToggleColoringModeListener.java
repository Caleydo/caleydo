/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
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
