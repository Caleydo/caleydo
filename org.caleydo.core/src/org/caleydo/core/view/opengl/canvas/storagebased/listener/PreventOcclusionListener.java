package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.PreventOcclusionEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

public class PreventOcclusionListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof PreventOcclusionEvent) {
			handler.preventOcclusion(((PreventOcclusionEvent) event).getFlag());
		}
	}

}
