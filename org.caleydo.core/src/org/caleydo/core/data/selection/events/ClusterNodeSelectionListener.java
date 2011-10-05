package org.caleydo.core.data.selection.events;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.ClusterNodeSelectionEvent;
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
