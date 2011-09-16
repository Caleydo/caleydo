package org.caleydo.core.view.listener;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;

public class ClearConnectionsListener
	extends AEventListener<ConnectedElementRepresentationManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearConnectionsEvent) {
			ClearConnectionsEvent clearConnectionsEvent = (ClearConnectionsEvent) event;
			IDType idType = clearConnectionsEvent.getIdType();
			handler.handleClearEvent(idType);
		}
	}

}
