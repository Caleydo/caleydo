/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;

public class EnableGeneMappingListener extends APathwayListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof PathwayMappingEvent) {
			PathwayMappingEvent pEvent = (PathwayMappingEvent) event;
			// handler.mapTablePerspective(pEvent.getTablePerspective());
		}
	}

}
