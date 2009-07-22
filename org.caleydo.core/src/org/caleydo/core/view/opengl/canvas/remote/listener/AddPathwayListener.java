package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;

public class AddPathwayListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwayEvent) {
			LoadPathwayEvent loadEvent = (LoadPathwayEvent) event;
			handler.addPathwayView(loadEvent.getPathwayID());
		}
	}

}
