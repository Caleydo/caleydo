package org.caleydo.view.parcoords.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.ResetAxisSpacingEvent;
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
