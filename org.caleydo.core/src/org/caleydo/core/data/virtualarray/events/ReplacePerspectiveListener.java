/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class ReplacePerspectiveListener
	extends AEventListener<IVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplacePerspectiveEvent) {
			ReplacePerspectiveEvent replaceEvent = ((ReplacePerspectiveEvent) event);

			handler.replacePerspective(replaceEvent.getEventSpace(), replaceEvent.getPerspectiveID(),
				replaceEvent.getPerspectiveInitializationData());
		}
	}
}
