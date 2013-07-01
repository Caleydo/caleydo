/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.datadomain.pathway.IPathwayHandler;

public class AddPathwayListener
 extends AEventListener<IPathwayHandler> {
	/**
 *
 */
	public AddPathwayListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwayEvent) {
			LoadPathwayEvent loadEvent = (LoadPathwayEvent) event;
			handler.addPathwayView(loadEvent.getPathwayID(), loadEvent.getEventSpace());
		}
	}

}
