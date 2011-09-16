package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.XAxisSelectorEvent;
import org.caleydo.view.scatterplot.GLScatterPlot;

/**
 * Listener that reacts events for setting the max. displayed hierarchy depth in
 * RadialHierarchy.
 * 
 * @author Juergen Pillhofer
 */

public class XAxisSelectorListener extends AEventListener<GLScatterPlot> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof XAxisSelectorEvent) {
			handler.setXAxis(((XAxisSelectorEvent) event).getSelectedAxis());

		}
	}
}
