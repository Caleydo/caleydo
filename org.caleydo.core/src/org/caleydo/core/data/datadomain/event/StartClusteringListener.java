/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StartClusteringEvent;

public class StartClusteringListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof StartClusteringEvent) {
			StartClusteringEvent startClusteringEvent = (StartClusteringEvent) event;
			if (handler.getDataDomainID() == startClusteringEvent.getEventSpace())
				handler.startClustering(startClusteringEvent.getClusterConfiguration());
		}
	}

}
