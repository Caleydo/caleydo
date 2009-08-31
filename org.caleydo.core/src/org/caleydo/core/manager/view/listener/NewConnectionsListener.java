package org.caleydo.core.manager.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.NewConnectionsEvent;
import org.caleydo.core.manager.view.ISelectionTransformer;

public class NewConnectionsListener
	extends AEventListener<ISelectionTransformer> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewConnectionsEvent) {
			handler.handleNewConnections();
		}
	}

}
