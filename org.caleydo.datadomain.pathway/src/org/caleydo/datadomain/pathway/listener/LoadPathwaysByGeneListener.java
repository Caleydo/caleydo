/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.IPathwayHandler;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LoadPathwaysByGeneListener extends AEventListener<IPathwayHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;

			if (loadEvent.getIdType().getIDCategory() == IDCategory.getIDCategory("GENE")) {
				Set<PathwayGraph> pathwayGraphs = PathwayManager.get()
						.getPathwayGraphsByGeneID(loadEvent.getIdType(),
								loadEvent.getGeneID());
				if (pathwayGraphs == null) {
					Logger.log(new Status(IStatus.WARNING, this.toString(),
							"No mapping found for Gene ID to pathway graphs."));
					return;
				}
				handler.loadDependentPathways(pathwayGraphs);
			} else
				throw new IllegalStateException("Not implemented!");
		}
	}
}
