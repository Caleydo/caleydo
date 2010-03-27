package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.HideHeatMapElementsEvent;
import org.caleydo.view.compare.GLCompare;

public class HideHeatMapElementsEventListener extends AEventListener<GLCompare> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof HideHeatMapElementsEvent) {
			handler.setHideHeatMapElements(((HideHeatMapElementsEvent) event)
					.isElementsHidden());
		}
	}

}
