package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;

public class ToggleNavigationModeListener extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.toggleNavigationMode();
	}
}