package org.caleydo.view.radial.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.radial.DetailOutsideEvent;
import org.caleydo.view.radial.GLRadialHierarchy;

public class DetailOutsideListener extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof DetailOutsideEvent) {
			DetailOutsideEvent detailOutsideEvent = (DetailOutsideEvent) event;
			handler.handleAlternativeSelection(detailOutsideEvent.getElementID());
		}

	}

}
