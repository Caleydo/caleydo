/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.actions;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.treemap.listener.ZoomInEvent;

/**
 * Action for zoom in function.
 *
 * @author Michael Lafer
 *
 */

public class ZoomInAction extends SimpleAction {

	private static final String LABEL = "Zoom";
	private static final String ICON = "resources/icons/general/search.png";

	public ZoomInAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ZoomInEvent());
		setChecked(false);
	}
}
