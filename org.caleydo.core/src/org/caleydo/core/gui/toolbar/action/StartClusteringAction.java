/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import java.util.ArrayList;

import org.caleydo.core.data.configuration.DataConfiguration;
import org.caleydo.core.data.configuration.DataConfigurationChooser;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
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

		ArrayList<ATableBasedDataDomain> availableDomains = DataDomainManager.get()
				.getDataDomainsByType(ATableBasedDataDomain.class);

		ArrayList<IDataDomain> tableBasedDataDomains = new ArrayList<IDataDomain>();
		for (ATableBasedDataDomain dataDomain : availableDomains) {
			tableBasedDataDomains.add(dataDomain);
		}
		
		DataConfiguration config = DataConfigurationChooser.determineDataConfiguration(
				tableBasedDataDomains, TEXT, true);
		
		StartClusteringDialog dialog = new StartClusteringDialog(new Shell(), (ATableBasedDataDomain) config.getDataDomain());
		dialog.setDimensionPerspective(config.getDimensionPerspective());
		dialog.setRecordPerspective(config.getRecordPerspective());
		
		dialog.open();
		ClusterConfiguration clusterState = dialog.getClusterState();
		if (clusterState == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)

		event = new StartClusteringEvent(clusterState);
		event.setDataDomainID(config.getDataDomain().getDataDomainID());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}
}