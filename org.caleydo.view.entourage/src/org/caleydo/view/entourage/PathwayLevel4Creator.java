package org.caleydo.view.entourage;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.pathway.IPathwayRendererCreator;

public class PathwayLevel4Creator implements IPathwayRendererCreator {

	public PathwayLevel4Creator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ALayoutRenderer create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {
		return new Level4PathwayRenderer(pathway);
	}

}
