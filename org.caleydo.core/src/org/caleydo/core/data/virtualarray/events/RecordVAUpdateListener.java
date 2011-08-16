package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.VirtualArrayDeltaEvent;

/**
 * Listener for dimension virtual array update events. This listener gets the payload from a
 * {@link VirtualArrayDeltaEvent} and calls a related {@link IVirtaualArrayUpdateHandler}.
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
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordVAUpdateEvent) {
			RecordVAUpdateEvent virtualArrayUpdateEvent = (RecordVAUpdateEvent) event;
			String info = virtualArrayUpdateEvent.getInfo();
			handler.handleRecordVAUpdate(info);
		}
	}

}
