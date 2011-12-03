package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.BrickHeatMapLayoutConfiguration;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHeatMap}.
 * 
 * @author Christian Partl
 * 
 */
public class HeatMapCreator implements IRemoteViewCreator {

	public HeatMapCreator() {
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLHeatMap heatMap = (GLHeatMap) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLHeatMap.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		heatMap.setRemoteRenderingGLView(remoteRenderingView);
		heatMap.setDataContainer(remoteRenderingView.getDataContainer());
		heatMap.setDataDomain(remoteRenderingView.getDataDomain());
		BrickHeatMapLayoutConfiguration template = new BrickHeatMapLayoutConfiguration(heatMap);
		heatMap.setRenderTemplate(template);
		heatMap.initialize();
		heatMap.initRemote(gl, remoteRenderingView, glMouseListener);

		return heatMap;
	}
}
