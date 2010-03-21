package org.caleydo.rcp.action.toolbar.view;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class StartClusteringAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/storagebased/clustering.png";

	private ISet set;

	/**
	 * Constructor.
	 */
	public StartClusteringAction() {
		super(-1);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
		dialog.open();
		ClusterState clusterState = dialog.getClusterState();

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)
		if (set == null)
			set = GeneralManager.get().getMasterUseCase().getSet();
		event = new StartClusteringEvent(clusterState, set.getID());
		// else if (clusterState != null)
		// event = new StartClusteringEvent(clusterState);

		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public void setSet(ISet set) {
		this.set = set;
	}
}