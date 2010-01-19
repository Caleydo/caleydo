package org.caleydo.view.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.view.radial.GLRadialHierarchy;

/**
 * Listener that reacts on go back in history events for RadialHierarchy.
 * 
 * @author Christian Partl
 */
public class GoBackInHistoryListener extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GoBackInHistoryEvent) {
			handler.goBackInHistory();
		}

	}

}
