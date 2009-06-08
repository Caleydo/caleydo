package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

public class ApplyCurrentSelectionToVirtualArrayListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		handler.saveSelection();
		
	}

}
