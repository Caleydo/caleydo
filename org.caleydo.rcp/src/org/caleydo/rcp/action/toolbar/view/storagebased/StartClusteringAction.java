package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.dialog.file.StartClusteringDialog;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class StartClusteringAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/storagebased/clustering.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public StartClusteringAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
		dialog.open();
		ClusterState clusterState = dialog.getClusterState();

		if (clusterState != null)
			GeneralManager.get().getEventPublisher().triggerEvent(new StartClusteringEvent(clusterState));

	}

}