package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;

/**
 * Listener for virtual array update events. This listener gets the payload from a {@link VADeltaEvent} and
 * calls a related {@link IVirtaualArrayUpdateHandler}.
 * 
 * @author Werner Puff
 */
public class RecordVADeltaListener
	extends AEventListener<IRecordVADeltaHandler> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload and calling the related
	 * handler
	 * 
	 * @param event
	 *            {@link SelectionUpdateEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordVADeltaEvent) {
			RecordVADeltaEvent virtualArrayUpdateEvent = (RecordVADeltaEvent) event;
			RecordVADelta delta = virtualArrayUpdateEvent.getVirtualArrayDelta();
			String info = virtualArrayUpdateEvent.getInfo();
			handler.handleRecordVADelta(delta, info);
		}
	}
}
