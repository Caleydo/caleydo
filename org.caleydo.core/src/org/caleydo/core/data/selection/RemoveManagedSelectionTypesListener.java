/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.RemoveManagedSelectionTypesEvent;

public class RemoveManagedSelectionTypesListener
	extends AEventListener<SelectionManager> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RemoveManagedSelectionTypesEvent) {
			handler.removeMangagedSelectionTypes();
		}
	}

}
