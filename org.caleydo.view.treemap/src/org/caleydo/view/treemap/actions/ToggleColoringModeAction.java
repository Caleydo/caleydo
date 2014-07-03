/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/

package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.view.treemap.listener.ToggleColoringModeEvent;

/**
 * Action for toggling coloring mode.
 *
 * @author Michael Lafer
 *
 */

public class ToggleColoringModeAction extends SimpleAction {

	private static final String LABEL = "Toggle ColorMode Average/Selected";
	private static final String ICON = "resources/icons/view/tablebased/clustering.png";

	public ToggleColoringModeAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		ToggleColoringModeEvent event = new ToggleColoringModeEvent();
		event.setCalculateColor(isChecked());
		
		EventPublisher.trigger(event);
	}

}

