/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.datadomain.listener;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.event.CreateClusteringEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.util.clusterer.gui.ClusterDialog;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Event handler for {@link CreateClusteringEvent}.
 *
 * @author Christian Partl
 *
 */
public class CreateClusteringEventListener extends AEventListener<ATableBasedDataDomain> {

	public CreateClusteringEventListener(ATableBasedDataDomain dataDomain) {
		setHandler(dataDomain);
		setExclusiveEventSpace(dataDomain.getDataDomainID());
	}

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateClusteringEvent) {
			CreateClusteringEvent createClusteringEvent = (CreateClusteringEvent) event;

			final ATableBasedDataDomain dataDomain = handler;
			final boolean isDimensionClustering = createClusteringEvent.isDimensionClustering();

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {

					ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
					clusterConfiguration.setSourceDimensionPerspective(dataDomain.getDefaultTablePerspective()
							.getDimensionPerspective());
					clusterConfiguration.setSourceRecordPerspective(dataDomain.getDefaultTablePerspective()
							.getRecordPerspective());

					clusterConfiguration.setModifyExistingPerspective(false);
					if (isDimensionClustering)
						clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
					else
						clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);

					ClusterDialog dialog = new ClusterDialog(new Shell(), dataDomain, clusterConfiguration);

					dialog.open();

				}
			});
		}
	}
}
