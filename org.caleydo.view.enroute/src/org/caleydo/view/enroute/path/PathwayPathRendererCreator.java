package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

public class PathwayPathRendererCreator implements IRemoteRendererCreator {

	public PathwayPathRendererCreator() {
	}

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {

		APathwayPathRenderer renderer = new EnRoutePathRenderer(remoteRenderingView, tablePerspectives);

		EventBasedSelectionManager geneSelectionManager = new EventBasedSelectionManager(renderer,
				IDType.getIDType("DAVID"));
		geneSelectionManager.registerEventListeners();

		EventBasedSelectionManager metaboliteSelectionManager = new EventBasedSelectionManager(renderer,
				IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		EventBasedSelectionManager sampleSelectionManager = null;
		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);
		if (dataDomains.size() != 0) {
			IDType sampleIDType = dataDomains.get(0).getSampleIDType().getIDCategory().getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(renderer, sampleIDType);
			sampleSelectionManager.registerEventListeners();
		}

		// renderer.setSampleSelectionManager(sampleSelectionManager);
		// renderer.setGeneSelectionManager(geneSelectionManager);
		// renderer.setMetaboliteSelectionManager(metaboliteSelectionManager);
		// renderer.setPathwayPathEventSpace(embeddingEventSpace);

		renderer.init();

		return renderer;
	}
}
