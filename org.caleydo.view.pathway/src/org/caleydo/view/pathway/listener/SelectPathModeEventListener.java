/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * Listener for {@link EnablePathSelectionEvent}.
 *
 * @author Christian Partl
 *
 */
public class SelectPathModeEventListener extends AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof EnablePathSelectionEvent) {
			handler.enablePathSelection(((EnablePathSelectionEvent) event).isPathSelectionMode());
		}
	}

}
