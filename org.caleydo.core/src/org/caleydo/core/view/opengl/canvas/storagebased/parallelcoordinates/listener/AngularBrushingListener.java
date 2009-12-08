package org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;

/**
 * Listener that reacts on angular brushing events for PCs.
 * 
 * @author Alexander Lex
 */
public class AngularBrushingListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AngularBrushingEvent) {
			handler.triggerAngularBrushing();
		}

	}

}
