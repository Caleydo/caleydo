package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;

public class AddPathwayListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwayEvent) {
			LoadPathwayEvent loadEvent = (LoadPathwayEvent) event;
			handler.addPathwayView(loadEvent.getPathwayID(), loadEvent.getDataDomainID());
		}
	}

}
