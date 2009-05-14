package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

public class UseRandomSamplingListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		
		if (event instanceof UseRandomSamplingEvent) {
			UseRandomSamplingEvent randomSamplingEvent = (UseRandomSamplingEvent) event;
			handler.useRandomSampling(randomSamplingEvent.getFlag());
		}
		
	}

}
