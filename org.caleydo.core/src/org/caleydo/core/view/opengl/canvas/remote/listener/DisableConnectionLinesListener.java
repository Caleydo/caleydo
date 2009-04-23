package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.manager.event.AEvent;

public class DisableConnectionLinesListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		bucket.setConnectionLinesEnabled(false);
	}

}
