package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;

public class DisableTexturesListener extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.setPathwayTexturesEnabled(false);
	}

}
