package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.HideHeatMapElementsEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class HideHeatMapElementsEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof HideHeatMapElementsEvent) {
			handler.setHideHeatMapElements(((HideHeatMapElementsEvent) event)
					.isElementsHidden());
		}
	}

}
