/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.view.treemap.listener.ToggleLabelEvent;

/**
 * Action for switching labels on/off.
 *
 * @author Michael Lafer
 *
 */

public class ToggleLabelAction extends SimpleAction {

	public static final String LABEL = "Toggle Labels";
	public static final String ICON = "resources/icons/view/hyperbolic/tree_switch_lin.png";

	public ToggleLabelAction() {
		super(LABEL, ICON);
		setChecked(true);
	}

	@Override
	public void run() {
		super.run();
		// System.out.println("label: "+isChecked());
		ToggleLabelEvent event = new ToggleLabelEvent();
		event.setDrawLabel(isChecked());
		
		EventPublisher.trigger(event);
	}
}
