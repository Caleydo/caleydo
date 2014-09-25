package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.embedding.IPathwayRepresentationCreator;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

public class EntourageLevel3PathwayCreator implements IPathwayRepresentationCreator {

	public EntourageLevel3PathwayCreator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IPathwayRepresentation create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {
		ContextualPathsRenderer renderer = new ContextualPathsRenderer(remoteRenderingView, embeddingEventSpace,
				pathway, tablePerspectives, false);
		renderer.init();

		return renderer;

	}

}
