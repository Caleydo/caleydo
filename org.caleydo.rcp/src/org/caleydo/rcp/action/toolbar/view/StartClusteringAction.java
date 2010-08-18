package org.caleydo.rcp.action.toolbar.view;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.ISetBasedDataDomain;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
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

	private ArrayList<ISet> sets;

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
		if (sets == null || sets.size() == 0) {
			sets = new ArrayList<ISet>();
			sets.add(((ISetBasedDataDomain) DataDomainManager.getInstance().getDataDomain(
				"org.caleydo.datadomain.genetic")).getSet());
		}
		for (ISet tmpSet : sets) {
			event = new StartClusteringEvent(clusterState, tmpSet.getID());
			event.setDataDomainType("org.caleydo.datadomain.genetic");
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}
	}

	public void setSets(ArrayList<ISet> sets) {
		this.sets = sets;
	}
}