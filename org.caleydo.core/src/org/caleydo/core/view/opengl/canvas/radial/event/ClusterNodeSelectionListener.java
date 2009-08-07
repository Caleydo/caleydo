package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * Listener for {@link ClusterNodeSelectionEvent}.
 * 
 * @author Christian Partl
 *
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
