package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.parcoords.GLParallelCoordinates;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLParallelCoordinates}.
 * 
 * @author Christian Partl
 * 
 */
public class ParCoordsCreator implements IRemoteViewCreator {

	public ParCoordsCreator() {
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLParallelCoordinates parCoords = (GLParallelCoordinates) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLParallelCoordinates.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		parCoords.setRemoteRenderingGLView(remoteRenderingView);
		parCoords.setDataContainer(remoteRenderingView.getDataContainer());
		parCoords.setDataDomain(remoteRenderingView.getDataDomain());
		parCoords.initialize();
		parCoords.initRemote(gl, remoteRenderingView, glMouseListener);
		parCoords.setDetailLevel(DetailLevel.LOW);

		return parCoords;
	}
}
