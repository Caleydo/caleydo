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
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * Listener for {@link PathwayPathSelectionEvent}.
 *
 * @author Christian
 *
 */
public class EnRoutePathEventListener extends AEventListener<GLPathway> {


	@Override
	public void handleEvent(AEvent event) {
		if(event.getSender() == handler)
			return;
		if (event instanceof PathwayPathSelectionEvent) {
			PathwayPathSelectionEvent e = (PathwayPathSelectionEvent) event;
			handler.setSelectedPathSegments(e.getPath());
		}
	}

}
