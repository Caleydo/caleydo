package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;

/**
 * Receiver of events dealing with cluster nodes.
 * 
 * @author Christian Partl
 */

public interface IClusterNodeEventReceiver extends IListenerOwner{

	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event);
}
