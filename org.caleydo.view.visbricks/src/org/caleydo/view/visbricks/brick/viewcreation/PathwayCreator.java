package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLPathway}.
 * 
 * @author Christian Partl
 * @author Marc Streit
 * 
 */
public class PathwayCreator implements IRemoteViewCreator {

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		//Collection<PathwayGraph> pathways = PathwayManager.get().getAllItems();

//		remoteRenderingView.GET
//		DataContainer pathwayDataContainer = remoteRenderingView.getDataContainer();
		

		
		GLPathway pathwayView = (GLPathway) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLPathway.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

//		pathwayView.setPathway(remoteRenderingView.);
		pathwayView.setRemoteRenderingGLView(remoteRenderingView);
		pathwayView.setDataDomain(remoteRenderingView.getDataDomain());
		pathwayView.setDataContainer(remoteRenderingView.getDataContainer());
//		pathwayView.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		pathwayView.initialize();
		pathwayView.initRemote(gl, remoteRenderingView, glMouseListener);

		return pathwayView;
	}

}
