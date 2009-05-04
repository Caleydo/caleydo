package org.caleydo.core.view.opengl.canvas.pathway.listeners;

import org.caleydo.core.manager.event.AEvent;

public class DisableNeighborhoodListener
	extends APathwayListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.enableNeighborhood(false);
	}

}
