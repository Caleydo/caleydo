package org.caleydo.core.view.listener;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Listener for selection update events. This listener gets the payload from a
 * SelectionUpdateEvent and calls a related {@link IExtendedSelectionUpdateHandler}. In
 * addition to the standard {@link ExtendedSelectionUpdateListener} the data method is
 * provided to the handler method.
 * 
 * @author Marc Streit
 */
public class ExtendedSelectionUpdateListener
	extends AEventListener<IExtendedSelectionUpdateHandler> {

	/**
	 * Handles {@link SelectionUdpateEvent}s by extracting the event's payload
	 * and calling the related handler
	 * 
	 * @param event {@link SelectionUpdateEvent} to handle, other events will be
	 *            ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionUpdateEvent) {
			SelectionUpdateEvent selectioUpdateEvent = (SelectionUpdateEvent) event;
			SelectionDelta delta = selectioUpdateEvent.getSelectionDelta();
			boolean scrollToSelection = selectioUpdateEvent.isScrollToSelection();
			String info = selectioUpdateEvent.getInfo();
			handler.handleSelectionUpdate(delta, scrollToSelection, info, event.getDataDomainID());
			VisLinkScene.resetAnimation(System.currentTimeMillis());
		}
	}
}
