/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.ClusterNodeSelectionEvent;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;

/**
 * Listener for {@link ClusterNodeSelectionEvent}.
 * 
 * @author Christian Partl
 */
public class ClusterNodeSelectionListener
	extends AEventListener<IClusterNodeEventReceiver> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof ClusterNodeSelectionEvent) {
			ClusterNodeSelectionEvent selectionEvent = (ClusterNodeSelectionEvent) event;
			handler.handleClusterNodeSelection(selectionEvent);
		}
	}

}
