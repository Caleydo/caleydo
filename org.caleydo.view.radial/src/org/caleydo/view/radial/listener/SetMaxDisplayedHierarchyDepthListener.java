/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.radial.GLRadialHierarchy;
import org.caleydo.view.radial.event.SetMaxDisplayedHierarchyDepthEvent;

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
			handler.setMaxDisplayedHierarchyDepth(((SetMaxDisplayedHierarchyDepthEvent) event)
					.getMaxDisplayedHierarchyDepth());
		}

	}

}
