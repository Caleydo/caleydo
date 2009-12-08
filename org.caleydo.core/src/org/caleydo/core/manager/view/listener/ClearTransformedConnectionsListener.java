package org.caleydo.core.manager.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.ClearTransformedConnectionsEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;

public class ClearTransformedConnectionsListener
	extends AEventListener<ConnectedElementRepresentationManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearTransformedConnectionsEvent) {
			handler.handleClearTransformedConnectionsEvent();
		}
	}

}
