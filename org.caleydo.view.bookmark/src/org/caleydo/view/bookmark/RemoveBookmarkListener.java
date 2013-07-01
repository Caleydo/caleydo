/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.RemoveBookmarkEvent;

public class RemoveBookmarkListener extends AEventListener<GLBookmarkView> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RemoveBookmarkEvent<?>) {
			RemoveBookmarkEvent<?> removeBookmarkEvent = (RemoveBookmarkEvent<?>) event;
			handler.handleRemoveBookmarkEvent(removeBookmarkEvent);
		}

	}

}
