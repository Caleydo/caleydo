package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Listener for selection update events, that do not belong to the dataDomain the events are specified for.
 * This is used only in dataDomains, where this can be translated to another dataDomainType.
 * 
 * @author Alexander Lex
 */
public class ForeignSelectionUpdateListener
	extends AEventListener<ATableBasedDataDomain> {

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
			boolean scrollToSelection = selectioUpdateEvent.isScrollToSelection();
			String info = selectioUpdateEvent.getInfo();
			handler.handleForeignSelectionUpdate(selectioUpdateEvent.getDataDomainID(), delta,
				scrollToSelection, info);
			VisLinkScene.resetAnimation(System.currentTimeMillis());
		}
	}

}
