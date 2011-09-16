package org.caleydo.view.info.listener;

import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.view.info.InfoArea;

/**
 * Listener for virtual array update events. This listener gets the payload from
 * a {@link VADeltaEvent} and calls a related
 * {@link IVirtaualArrayUpdateHandler}.
 * 
 * @author Werner Puff
 */
public class InfoAreaUpdateListener extends AEventListener<InfoArea> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload
	 * and calling the related handler
	 * 
	 * @param event
	 *            {@link SelectionUpdateEvent} to handle, other events will be
	 *            ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InfoAreaUpdateEvent) {
			InfoAreaUpdateEvent infoAreaUpdateEvent = (InfoAreaUpdateEvent) event;
			String info = infoAreaUpdateEvent.getInfo();
			handler.handleInfoAreaUpdate(info);
		}
	}

}
