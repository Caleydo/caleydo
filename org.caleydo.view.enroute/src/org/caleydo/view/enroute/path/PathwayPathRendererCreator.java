package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

public class PathwayPathRendererCreator implements IRemoteRendererCreator {

	public PathwayPathRendererCreator() {
	}

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {

		APathwayPathRenderer renderer = new VerticalPathRenderer(remoteRenderingView, tablePerspectives, false);
		renderer.setPathwayPathEventSpace(embeddingEventSpace);
		renderer.setSizeConfig(PathSizeConfiguration.COMPACT);
		renderer.init();

		return renderer;
	}
}
