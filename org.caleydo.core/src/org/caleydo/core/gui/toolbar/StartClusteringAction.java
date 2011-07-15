package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
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

	private ArrayList<ISet> sets;

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

		// FIXME replace with dynamic guess based version when it works
		// ASetBasedDataDomain dataDomain =
		// DataDomainManager.getInstance().guessDataDomain(ASetBasedDataDomain.class);
		ASetBasedDataDomain dataDomain =
			(ASetBasedDataDomain) DataDomainManager.get().getDataDomainByID("org.caleydo.datadomain.genetic");

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell(), dataDomain);
		dialog.open();
		ClusterState clusterState = dialog.getClusterState();
		if (clusterState == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)
		if (sets == null || sets.size() == 0) {
			sets = new ArrayList<ISet>();

			sets.add(dataDomain.getSet());
		}
		for (ISet tmpSet : sets) {
			event = new StartClusteringEvent(clusterState, tmpSet.getID());
			event.setDataDomainType(dataDomain.getDataDomainID());
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}
	}

	public void setSets(ArrayList<ISet> sets) {
		this.sets = sets;
	}
}