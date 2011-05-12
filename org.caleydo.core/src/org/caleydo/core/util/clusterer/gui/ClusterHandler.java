package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.gui.toolbar.StartClusteringAction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class ClusterHandler
	extends AbstractHandler
	implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		StartClusteringAction action = new StartClusteringAction();
		action.run();

		// StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
		// dialog.open();
		// ClusterState clusterState = dialog.getClusterState();
		//
		// if (clusterState != null)
		// GeneralManager.get().getEventPublisher().triggerEvent(new StartClusteringEvent(clusterState));
		//
		return null;
	}
}
