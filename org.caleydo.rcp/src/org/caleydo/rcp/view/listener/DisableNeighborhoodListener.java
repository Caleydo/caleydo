package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;

public class DisableNeighborhoodListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.setNeighborhoodEnabled(false);
	}

}
