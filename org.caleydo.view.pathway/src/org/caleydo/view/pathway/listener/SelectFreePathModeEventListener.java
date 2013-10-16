/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * @author Christian
 *
 */
public class SelectFreePathModeEventListener extends AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof EnableFreePathSelectionEvent) {
			EnableFreePathSelectionEvent e = (EnableFreePathSelectionEvent) event;
			handler.enableFreePathSelection(e.isEnabled());
		}

	}

}
