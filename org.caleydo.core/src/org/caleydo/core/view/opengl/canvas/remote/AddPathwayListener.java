package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.bucket.LoadPathwayEvent;

public class AddPathwayListener
	implements IEventListener {

	private GLRemoteRendering bucket = null;
	
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwayEvent) {
			LoadPathwayEvent loadEvent = (LoadPathwayEvent) event;
			System.out.println("load pathway with id " + loadEvent.getPathwayID());
			bucket.addPathwayView(loadEvent.getPathwayID());
		}
	}

	public GLRemoteRendering getBucket() {
		return bucket;
	}

	public void setBucket(GLRemoteRendering bucket) {
		this.bucket = bucket;
	}

}
