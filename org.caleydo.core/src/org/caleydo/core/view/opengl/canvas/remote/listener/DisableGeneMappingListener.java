package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.manager.event.AEvent;

public class DisableGeneMappingListener
	extends ARemoteRenderingListener {


	@Override
	public void handleEvent(AEvent event) {
		handler.setGeneMappingEnabled(false);
	}

}
