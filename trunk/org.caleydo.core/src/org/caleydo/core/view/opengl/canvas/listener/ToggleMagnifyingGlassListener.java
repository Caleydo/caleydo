package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

public class ToggleMagnifyingGlassListener
	extends AEventListener<AGLEventListener> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ToggleMagnifyingGlassEvent) {
			handler.handleToggleMagnifyingGlassEvent();
		}
	}

}
