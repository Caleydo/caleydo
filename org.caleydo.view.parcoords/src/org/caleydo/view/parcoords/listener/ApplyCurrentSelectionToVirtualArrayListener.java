package org.caleydo.view.parcoords.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.parcoords.GLParallelCoordinates;

public class ApplyCurrentSelectionToVirtualArrayListener extends
		AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		handler.saveSelection();

	}

}
