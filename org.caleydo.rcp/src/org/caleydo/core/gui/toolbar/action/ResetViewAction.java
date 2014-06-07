/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.event.view.ResetAllViewsEvent;
import org.caleydo.core.gui.SimpleEventAction;

public class ResetViewAction extends SimpleEventAction {
	private static final String LABEL = "Reset View";
	private static final String ICON = "resources/icons/view/general/reset_view.png";

	public ResetViewAction() {
		super(LABEL, ICON, new ResetAllViewsEvent());
	}
}
