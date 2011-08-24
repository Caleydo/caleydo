package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * Listener for dimension virtual array update events. This listener gets the payload from a
 * {@link VADeltaEvent} and calls a related {@link IVirtaualArrayUpdateHandler}.
 * 
 * @author Alexander Lex
 */
public class RecordVAUpdateListener
	extends AEventListener<IRecordVAUpdateHandler> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload and calling the related
	 * handler
	 * 
	 * @param event
	 *            {@link DimensionVAUpdateEvent} to handle, other events will be ignored
	 * @param
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordVAUpdateEvent) {
			RecordVAUpdateEvent virtualArrayUpdateEvent = (RecordVAUpdateEvent) event;

			handler.handleRecordVAUpdate(virtualArrayUpdateEvent.getPerspectiveID());
		}
	}

}
