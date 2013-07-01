/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.SelectionUpdateEvent;

/**
 * Listener for virtual array update events. This listener gets the payload from a {@link VADeltaEvent} and
 * calls a related {@link IVirtaualArrayUpdateHandler}.
 *
 * @author Werner Puff
 */
public class VADeltaListener
	extends AEventListener<IVADeltaHandler> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload and calling the related
	 * handler
	 *
	 * @param event
	 *            {@link SelectionUpdateEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof VADeltaEvent) {
			VADeltaEvent virtualArrayUpdateEvent = (VADeltaEvent) event;
			VirtualArrayDelta delta = virtualArrayUpdateEvent.getVirtualArrayDelta();
			String info = virtualArrayUpdateEvent.getInfo();
			handler.handleVADelta(delta, info);
		}
	}
}
