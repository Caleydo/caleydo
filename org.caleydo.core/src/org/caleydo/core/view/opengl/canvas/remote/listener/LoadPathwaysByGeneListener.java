package org.caleydo.core.view.opengl.canvas.remote.listener;

import java.util.ArrayList;

import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.bucket.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;

public class LoadPathwaysByGeneListener
	extends ARemoteRenderingListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;
			System.out.println("load pathways by gene ID " + loadEvent.getGeneID());

			convertGeneIDToVertexList(loadEvent.getIdType(), loadEvent.getGeneID());
		}
	}

	private void convertGeneIDToVertexList(EIDType idType, int geneID) {

		int iGraphItemID = 0;
		Integer iDavidID = -1;
		ArrayList<ICaleydoGraphItem> alPathwayVertexGraphItem = new ArrayList<ICaleydoGraphItem>();

		if (idType == EIDType.REFSEQ_MRNA_INT) {
			iDavidID =
				GeneralManager.get().getIDMappingManager()
					.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, geneID);
		}
		else if (idType == EIDType.DAVID)
			iDavidID = geneID;
		else
			return;

		if (iDavidID == null || iDavidID == -1)
			throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");

		iGraphItemID =
			GeneralManager.get().getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidID);

		if (iGraphItemID == -1) {
			return;
		}

		PathwayVertexGraphItem tmpPathwayVertexGraphItem =
			(PathwayVertexGraphItem) GeneralManager.get().getPathwayItemManager().getItem(iGraphItemID);

		if (tmpPathwayVertexGraphItem == null) {
			return;
		}

		alPathwayVertexGraphItem.add(tmpPathwayVertexGraphItem);

		if (!alPathwayVertexGraphItem.isEmpty()) {
			bucket.loadDependentPathways(alPathwayVertexGraphItem);
		}
	}
}
