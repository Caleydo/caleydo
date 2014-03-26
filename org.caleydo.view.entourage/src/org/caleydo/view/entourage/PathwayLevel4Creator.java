package org.caleydo.view.entourage;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.embedding.IPathwayRepresentationCreator;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

public class PathwayLevel4Creator implements IPathwayRepresentationCreator {

	public PathwayLevel4Creator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IPathwayRepresentation create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {
		return new Level4PathwayRenderer(pathway);
	}

}
