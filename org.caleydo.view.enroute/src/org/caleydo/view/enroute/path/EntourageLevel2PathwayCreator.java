package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.pathway.IPathwayRendererCreator;

public class EntourageLevel2PathwayCreator implements IPathwayRendererCreator {

	public EntourageLevel2PathwayCreator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ALayoutRenderer create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {

		ContextualPathsRenderer renderer = new ContextualPathsRenderer(remoteRenderingView, embeddingEventSpace,
				pathway, tablePerspectives, true);
		renderer.init();

		return renderer;

	}

}
