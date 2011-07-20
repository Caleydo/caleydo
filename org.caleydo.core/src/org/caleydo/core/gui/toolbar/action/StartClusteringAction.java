package org.caleydo.core.gui.toolbar.action;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.gui.StartClusteringDialog;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class StartClusteringAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/storagebased/clustering.png";

	private ArrayList<DataTable> sets;

	/**
	 * Constructor.
	 */
	public StartClusteringAction() {

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		ATableBasedDataDomain dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get().getDataDomainByType("org.caleydo.datadomain.genetic");

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell(), dataDomain);
		dialog.open();
		ClusterState clusterState = dialog.getClusterState();
		if (clusterState == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)
		if (sets == null || sets.size() == 0) {
			sets = new ArrayList<DataTable>();

			sets.add(dataDomain.getDataTable());
		}
		for (DataTable tmpSet : sets) {
			event = new StartClusteringEvent(clusterState, tmpSet.getID());
			event.setDataDomainID(dataDomain.getDataDomainID());
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}
	}

	public void setSets(ArrayList<DataTable> sets) {
		this.sets = sets;
	}
}