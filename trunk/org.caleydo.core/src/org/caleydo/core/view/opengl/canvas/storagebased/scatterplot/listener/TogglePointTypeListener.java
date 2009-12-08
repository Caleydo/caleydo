package org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.TogglePointTypeEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;


/**
 * Listener for
 * 
 * @author Jürgen Pillhofer
 */
public class TogglePointTypeListener
	extends AEventListener<GLScatterplot> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TogglePointTypeEvent) {
		
			handler.togglePointType();
		}
	}

}

