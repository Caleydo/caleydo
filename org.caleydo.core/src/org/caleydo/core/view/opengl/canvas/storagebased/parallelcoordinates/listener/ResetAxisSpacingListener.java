package org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;

/**
 * Listener for
 * 
 * @author Alexander Lex
 */
public class ResetAxisSpacingListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ResetAxisSpacingEvent) {
			handler.resetAxisSpacing();
		}
	}

}
