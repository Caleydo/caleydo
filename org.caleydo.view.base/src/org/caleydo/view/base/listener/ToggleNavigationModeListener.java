package org.caleydo.view.base.listener;

import org.caleydo.core.manager.event.AEvent;

public class ToggleNavigationModeListener extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.toggleNavigationMode();
	}
}