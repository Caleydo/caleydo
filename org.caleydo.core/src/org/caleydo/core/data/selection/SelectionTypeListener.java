/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

public class SelectionTypeListener
	extends AEventListener<SelectionManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionTypeEvent) {
			SelectionTypeEvent typeEvent = (SelectionTypeEvent) event;
			if (typeEvent.isRemove())
				handler.removeSelectionType(typeEvent.getSelectionType());
			else if (typeEvent.isCurrent())
				handler.setSelectionType(typeEvent.getSelectionType());
			else
				handler.addSelectionType(typeEvent.getSelectionType());
		}
	}
}
