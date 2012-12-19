package org.caleydo.view.pathway;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;

/**
 * Remote view creator for {@link GLPathway}. TablePerspectives provided to this creator must be an instance of
 * {@link PathwayTablePerspective}
 *
 * @author Christian Partl
 *
 */
public class PathwayRemoteViewCreator implements IRemoteViewCreator {

	public PathwayRemoteViewCreator() {
	}

	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives) {

		GLPathway pathwayView = (GLPathway) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLPathway.class, remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			if (!(tablePerspective instanceof PathwayTablePerspective)) {
				throw new IllegalArgumentException(
						"The provided table perspective must be of type PathwayTablePerspective.");
			}

			pathwayView.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
			pathwayView.setDataDomain(tablePerspective.getDataDomain());
			pathwayView.setTablePerspective(tablePerspective);
		}
		// pathwayView.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		pathwayView.initialize();

		return pathwayView;
	}

}
