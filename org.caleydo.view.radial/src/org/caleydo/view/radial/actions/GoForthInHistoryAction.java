/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.actions;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.radial.event.GoForthInHistoryEvent;

public class GoForthInHistoryAction extends SimpleAction {

	public static final String LABEL = "Forth";
	public static final String ICON = "resources/icons/view/general/redo.png";

	public GoForthInHistoryAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		
		EventPublisher.INSTANCE
				.triggerEvent(new GoForthInHistoryEvent());
		setChecked(false);
	}
}
