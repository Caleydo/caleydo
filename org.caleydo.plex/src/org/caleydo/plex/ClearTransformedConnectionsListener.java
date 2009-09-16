package org.caleydo.plex;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.ClearTransformedConnectionsEvent;

public class ClearTransformedConnectionsListener
	extends AEventListener<DeskothequeManager> {

	@Override
	public void handleEvent(AEvent event) {
		System.out.println("***** clear transformed desko");
		if (event instanceof ClearTransformedConnectionsEvent) {
			handler.clearConnections();
		}
	}

}
