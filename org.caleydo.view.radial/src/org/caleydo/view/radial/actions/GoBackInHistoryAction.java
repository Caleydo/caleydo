/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.actions;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.radial.event.GoBackInHistoryEvent;

public class GoBackInHistoryAction extends SimpleAction {

	public static final String LABEL = "Back";
	public static final String ICON = "resources/icons/view/general/undo.png";

	public GoBackInHistoryAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		
		EventPublisher.trigger(new GoBackInHistoryEvent());
		setChecked(false);
	}
}
