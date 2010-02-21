package org.caleydo.view.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.view.radial.GLRadialHierarchy;

/**
 * Listener that reacts events for setting the max. displayed hierarchy depth in
 * RadialHierarchy.
 * 
 * @author Christian
 */
public class SetMaxDisplayedHierarchyDepthListener extends
		AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SetMaxDisplayedHierarchyDepthEvent) {
			handler
					.setMaxDisplayedHierarchyDepth(((SetMaxDisplayedHierarchyDepthEvent) event)
							.getMaxDisplayedHierarchyDepth());
		}

	}

}
