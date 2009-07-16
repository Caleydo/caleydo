package org.caleydo.rcp.command.handler;

import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.rcp.action.toolbar.view.storagebased.StartClusteringAction;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public class ClusterHandler
	extends AbstractHandler
	implements IHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		StartClusteringAction action = new StartClusteringAction();
		action.run();
		
//		StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
//		dialog.open();
//		ClusterState clusterState = dialog.getClusterState();
//
//		if (clusterState != null)
//			GeneralManager.get().getEventPublisher().triggerEvent(new StartClusteringEvent(clusterState));
//	
		return null;
	}
}
