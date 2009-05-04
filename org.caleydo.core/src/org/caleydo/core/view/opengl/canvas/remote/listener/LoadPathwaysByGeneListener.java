package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;

public class LoadPathwaysByGeneListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;

			if (loadEvent.getIdType() == EIDType.DAVID || loadEvent.getIdType() == EIDType.REFSEQ_MRNA_INT) {
				handler.loadDependentPathways(GeneticIDMappingHelper.get().getPathwayGraphsByGeneID(loadEvent.getIdType(), loadEvent.getGeneID()));
			}
			else
				throw new IllegalStateException("Not implemented!");
		}
	}
}
