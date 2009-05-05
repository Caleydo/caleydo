package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;

/**
 * Listener for virtual array update events.
 * This listener gets the payload from a {@link VirtualArrayUpdateEvent} and calls 
 * a related {@link IVirtaualArrayUpdateHandler}. 
 * @author Werner Puff
 */
public class VirtualArrayUpdateListener 
	extends AEventListener<IVirtualArrayUpdateHandler> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload 
	 * and calling the related handler
	 * @param event {@link SelectionUpdateEvent} to handle, other events will be ignored 
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof VirtualArrayUpdateEvent) {
			VirtualArrayUpdateEvent virtualArrayUpdateEvent = (VirtualArrayUpdateEvent) event; 
			IVirtualArrayDelta delta = virtualArrayUpdateEvent.getVirtualArrayDelta();
			String info = virtualArrayUpdateEvent.getInfo();
			handler.handleVirtualArrayUpdate(delta, info);
		}
	}

}
