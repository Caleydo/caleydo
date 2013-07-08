/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection.events;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.SelectionUpdateEvent;

/**
 * Listener for selection update events. This listener gets the payload from a SelectionUpdateEvent and calls
 * a related {@link ISelectionHandler}.
 * 
 * @author Werner Puff
 */
public class SelectionUpdateListener
	extends AEventListener<ISelectionHandler> {

	/**
	 * Handles {@link SelectionUdpateEvent}s by extracting the event's payload and calling the related handler
	 * 
	 * @param event
	 *            {@link SelectionUpdateEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionUpdateEvent) {
			SelectionUpdateEvent selectioUpdateEvent = (SelectionUpdateEvent) event;
			SelectionDelta delta = selectioUpdateEvent.getSelectionDelta();
			handler.handleSelectionUpdate(delta);
		}
	}

}
