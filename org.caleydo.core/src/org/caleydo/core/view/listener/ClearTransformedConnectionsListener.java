package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.selection.ClearTransformedConnectionsEvent;
import org.caleydo.core.view.ConnectedElementRepresentationManager;

public class ClearTransformedConnectionsListener
	extends AEventListener<ConnectedElementRepresentationManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearTransformedConnectionsEvent) {
			handler.handleClearTransformedConnectionsEvent();
		}
	}

}
