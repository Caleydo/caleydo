package org.caleydo.view.bucket.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.view.bucket.GLRemoteRendering;
import org.caleydo.view.bucket.SerializedRemoteRenderingView;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLRemoteRendering(glCanvas, label, viewFrustum, ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedRemoteRenderingView();
	}
}
