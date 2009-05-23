package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Receiver of events dealing with cluster nodes
 * 
 * @author Christian Partl
 */

public interface IClusterNodeEventReceiver extends IListenerOwner{

	public void handleClusterNodeSelection(int iClusterNumber, ESelectionType selectionType);
}
