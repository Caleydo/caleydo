/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.view.stratomex.GLStratomex;

/**
 * @author alexsb
 *
 */
public class ReplaceTablePerspectiveListener extends AEventListener<GLStratomex> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceTablePerspectiveEvent) {
			ReplaceTablePerspectiveEvent rEvent = (ReplaceTablePerspectiveEvent) event;
			if (handler == rEvent.getReceiver()) {
				handler.replaceTablePerspective(rEvent.getNewPerspective(),
						rEvent.getOldPerspective());
			}
		}

	}
}
