package org.caleydo.view.parcoords;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;

/**
 * Default remote view creator for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 *
 */
public class ParCoordsRemoteViewCreator implements IRemoteViewCreator {

	public ParCoordsRemoteViewCreator() {
	}

	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives) {

		GLParallelCoordinates parCoords = (GLParallelCoordinates) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLParallelCoordinates.class, remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));
		parCoords.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);

		TablePerspective tablePerspective;

		if (tablePerspectives.size() > 0) {
			tablePerspective = tablePerspectives.get(0);
			parCoords.setTablePerspective(tablePerspective);
			parCoords.setDataDomain(tablePerspective.getDataDomain());
		}
		parCoords.initialize();

		return parCoords;
	}

}
