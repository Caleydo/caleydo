package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class ToggleMagnifyingGlassListener
	extends AEventListener<AGLView> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ToggleMagnifyingGlassEvent) {
			handler.handleToggleMagnifyingGlassEvent();
		}
	}

}
