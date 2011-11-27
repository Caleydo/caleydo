package org.caleydo.view.visbricks.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.kaplanmeier.GLKaplanMeier;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLKaplanMeier}.
 * 
 * @author Marc Streit
 * 
 */
public class KaplanMeierCreator implements IRemoteViewCreator {

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLKaplanMeier view = (GLKaplanMeier) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLKaplanMeier.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		view.setRemoteRenderingGLView(remoteRenderingView);
		view.setDataDomain(remoteRenderingView.getDataDomain());
		view.setDataContainer(remoteRenderingView.getDataContainer());
		view.initialize();
		view.initRemote(gl, remoteRenderingView, glMouseListener);

		return view;
	}
}
