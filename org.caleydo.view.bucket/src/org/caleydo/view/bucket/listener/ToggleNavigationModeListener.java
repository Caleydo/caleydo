package org.caleydo.view.bucket.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.listener.ARemoteRenderingListener;

public class ToggleNavigationModeListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.toggleNavigationMode();
	}
}