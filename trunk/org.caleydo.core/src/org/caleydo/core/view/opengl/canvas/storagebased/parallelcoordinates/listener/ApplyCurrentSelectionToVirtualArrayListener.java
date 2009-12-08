package org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;

public class ApplyCurrentSelectionToVirtualArrayListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		handler.saveSelection();

	}

}
