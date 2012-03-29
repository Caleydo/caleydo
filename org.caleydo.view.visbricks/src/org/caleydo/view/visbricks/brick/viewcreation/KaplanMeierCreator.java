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
 * @author Alexander Lex
 * 
 */
public class KaplanMeierCreator implements IRemoteViewCreator {

	public KaplanMeierCreator() {
	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl,
			GLMouseListener glMouseListener) {

		GLKaplanMeier kaplanMeier = (GLKaplanMeier) GeneralManager
				.get()
				.getViewManager()
				.createGLView(
						GLKaplanMeier.class,
						remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		kaplanMeier.setRemoteRenderingGLView(remoteRenderingView);
		kaplanMeier.setDataContainer(remoteRenderingView.getDataContainer());
		kaplanMeier.setDataDomain(remoteRenderingView.getDataDomain());

		kaplanMeier.initialize();
		kaplanMeier.initRemote(gl, remoteRenderingView, glMouseListener);

		return kaplanMeier;
	}
}
