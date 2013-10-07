package org.caleydo.view.pathway;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.pathway.IPathwayViewCreator;

public class EntouragePathwayCreator implements IPathwayViewCreator {

	public EntouragePathwayCreator() {
	}

	@Override
	public AGLView create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace) {
		GLPathway pathwayView = (GLPathway) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLPathway.class, remoteRenderingView.getParentGLCanvas(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		pathwayView.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
		pathwayView.setPathway(pathway);
		for (TablePerspective tablePerspective : tablePerspectives) {
			pathwayView.addTablePerspective(tablePerspective);
		}
		pathwayView.setOnNodeMappingTablePerspective(mappingTablePerspective);

		pathwayView.setPathwayPathEventSpace(embeddingEventSpace);
		// pathwayView.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		pathwayView.initialize();

		return pathwayView;
	}

}
