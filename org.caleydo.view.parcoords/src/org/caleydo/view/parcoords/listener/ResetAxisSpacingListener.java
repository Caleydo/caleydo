package org.caleydo.view.parcoords.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.ResetAxisSpacingEvent;
import org.caleydo.view.parcoords.GLParallelCoordinates;

/**
 * Listener for
 * 
 * @author Alexander Lex
 */
public class ResetAxisSpacingListener extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ResetAxisSpacingEvent) {
			handler.resetAxisSpacing();
		}
	}

}
