package org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.ResetScatterSelectionEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;

/**
 * Listener for
 * 
 * @author Juergen Pillhofer
 */
public class ResetSelectionListener
	extends AEventListener<GLScatterplot> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ResetScatterSelectionEvent) {

			handler.ResetSelection();
		}
	}

}
