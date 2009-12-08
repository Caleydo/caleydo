package org.caleydo.core.manager.view.listener;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;

public class ClearConnectionsListener
	extends AEventListener<ConnectedElementRepresentationManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearConnectionsEvent) {
			ClearConnectionsEvent clearConnectionsEvent = (ClearConnectionsEvent) event;
			EIDType idType = clearConnectionsEvent.getIdType();
			handler.handleClearEvent(idType);
		}
	}

}
