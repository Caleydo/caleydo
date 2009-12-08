package org.caleydo.core.view.opengl.canvas.hyperbolic.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.hyperbolic.SetMaxLayoutDepthEvent;
import org.caleydo.core.view.opengl.canvas.hyperbolic.GLHyperbolic;


public class SetMaxLayoutDepthListener 	
extends AEventListener<GLHyperbolic> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SetMaxLayoutDepthEvent) {
			handler.setMaxLayoutDepth(((SetMaxLayoutDepthEvent) event)
				.getMaxLayoutDepth());
		}

	}

}
