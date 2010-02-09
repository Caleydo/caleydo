package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.Toggle2AxisEvent;
import org.caleydo.view.scatterplot.GLScatterplot;

/**
 * Listener for
 * 
 * @author Juergen Pillhofer
 */
public class Toggle2AxisModeListener extends AEventListener<GLScatterplot> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof Toggle2AxisEvent) {

			handler.toggleSpecialAxisMode();
		}
	}

}
