package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.MinSizeAppliedEvent;
import org.caleydo.view.datagraph.GLDataGraph;

public class MinSizeAppliedEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MinSizeAppliedEvent) {
			MinSizeAppliedEvent minSizeAppliedEvent = (MinSizeAppliedEvent) event;
			if (minSizeAppliedEvent.getView() == handler) {
				handler.setMinSizeApplied(true);
			}
		}

	}

}
