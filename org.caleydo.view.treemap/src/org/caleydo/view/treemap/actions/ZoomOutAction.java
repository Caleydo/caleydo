/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.view.treemap.listener.ZoomOutEvent;

/**
 * Action for zoom out function.
 *
 * @author Michael Lafer
 *
 */

public class ZoomOutAction extends SimpleAction {

	private static final String LABEL = "Zoom Out";
	private static final String ICON = "resources/icons/view/general/undo.png";

	public ZoomOutAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		

		EventPublisher.trigger(new ZoomOutEvent());
		setChecked(false);
	}
}
