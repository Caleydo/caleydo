package org.caleydo.core.view.opengl.canvas.remote.listener;

import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;
import org.eclipse.core.runtime.Status;

public class LoadPathwaysByGeneListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;

			if (loadEvent.getIdType() == EIDType.DAVID || loadEvent.getIdType() == EIDType.REFSEQ_MRNA_INT) {
				Set<PathwayGraph> pathwayGraphs =
					GeneticIDMappingHelper.get().getPathwayGraphsByGeneID(loadEvent.getIdType(),
						loadEvent.getGeneID());
				if (pathwayGraphs == null)
				{
					GeneralManager.get().getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
						"No mapping found for Gene ID to pathway graphs."));
					return;
				}
				handler.loadDependentPathways(pathwayGraphs);
			}
			else
				throw new IllegalStateException("Not implemented!");
		}
	}
}
