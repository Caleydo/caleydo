package org.caleydo.core.gui.toolbar.action;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.util.clusterer.gui.StartClusteringDialog;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class StartClusteringAction
	extends AToolBarAction
	implements IToolBarItem {

	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/tablebased/clustering.png";

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
			(ATableBasedDataDomain) DataDomainManager.get().getDataDomainByType(
				"org.caleydo.datadomain.genetic");

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell(), dataDomain);
		dialog.open();
		ClusterConfiguration clusterState = dialog.getClusterState();
		if (clusterState == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)

		event = new StartClusteringEvent(clusterState);
		event.setDataDomainID(dataDomain.getDataDomainID());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}
}