package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.UseRandomSamplingEvent;
import org.caleydo.view.scatterplot.GLScatterPlot;

public class UseRandomSamplingListener extends AEventListener<GLScatterPlot> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof UseRandomSamplingEvent) {
			UseRandomSamplingEvent randomSamplingEvent = (UseRandomSamplingEvent) event;
			handler.useRandomSampling(randomSamplingEvent.getFlag());
		}

	}

}
