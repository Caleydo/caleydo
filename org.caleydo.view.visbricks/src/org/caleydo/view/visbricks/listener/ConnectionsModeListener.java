package org.caleydo.view.visbricks.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.dimensionbased.ConnectionsModeEvent;
import org.caleydo.view.visbricks.GLVisBricks;

/**
 * Listener for trend highlight mode changes.
 * 
 * @author Marc Streit
 */
public class ConnectionsModeListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ConnectionsModeEvent) {

			ConnectionsModeEvent connectionsModeEvent = (ConnectionsModeEvent) event;
			handler.handleTrendHighlightMode(connectionsModeEvent.isConnectionsOn(),
					connectionsModeEvent.isConnectionsHighlightDynamic(),
					connectionsModeEvent.getFocusFactor());
		}
	}

}
