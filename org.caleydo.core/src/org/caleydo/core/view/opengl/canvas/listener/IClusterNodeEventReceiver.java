/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.ClusterNodeSelectionEvent;

/**
 * Receiver of events dealing with cluster nodes.
 * 
 * @author Christian Partl
 */

public interface IClusterNodeEventReceiver
	extends IListenerOwner {

	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event);
}
