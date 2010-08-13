package org.caleydo.datadomain.pathway.listener;

import java.util.Set;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.GeneticIDMappingHelper;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LoadPathwaysByGeneListener extends APathwayLoaderListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;

			if (loadEvent.getIdType().getIDCategory() == IDCategory.getIDCategory("GENE")) {
				Set<PathwayGraph> pathwayGraphs = GeneticIDMappingHelper.get()
						.getPathwayGraphsByGeneID(loadEvent.getIdType(),
								loadEvent.getGeneID());
				if (pathwayGraphs == null) {
					GeneralManager
							.get()
							.getLogger()
							.log(new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
									"No mapping found for Gene ID to pathway graphs."));
					return;
				}
				handler.loadDependentPathways(pathwayGraphs);
			} else
				throw new IllegalStateException("Not implemented!");
		}
	}
}
