package org.caleydo.view.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.ChangeColorModeEvent;
import org.caleydo.view.radial.GLRadialHierarchy;

/**
 * Listener that reacts on change color mode events for RadialHierarchy.
 * 
 * @author Christian Partl
 */
public class ChangeColorModeListener extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ChangeColorModeEvent) {
			handler.changeColorMode();
		}

	}

}
