package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.dimensionbased.YAxisSelectorEvent;
import org.caleydo.view.scatterplot.GLScatterPlot;

/**
 * Listener that reacts events for setting the max. displayed hierarchy depth in
 * RadialHierarchy.
 * 
 * @author Juergen Pillhofer
 */

public class YAxisSelectorListener extends AEventListener<GLScatterPlot> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof YAxisSelectorEvent) {
			handler.setYAxis(((YAxisSelectorEvent) event).getSelectedAxis());

		}
	}
}
