package org.caleydo.core.view.opengl.canvas.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;

public class SetMaxDisplayedHierarchyDepthListener
	extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SetMaxDisplayedHierarchyDepthEvent) {
			handler.setMaxDisplayedHierarchyDepth(((SetMaxDisplayedHierarchyDepthEvent) event)
				.getMaxDisplayedHierarchyDepth());
		}

	}

}
