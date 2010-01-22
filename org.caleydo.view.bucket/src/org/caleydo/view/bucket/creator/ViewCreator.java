package org.caleydo.view.bucket.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.view.bucket.GLBucket;
import org.caleydo.view.bucket.SerializedRemoteRenderingView;
import org.caleydo.view.bucket.toolbar.RemoteRenderingToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLBucket(glCanvas, label, viewFrustum,
				ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedRemoteRenderingView();
	}

	@Override
	public Object createToolBarContent() {
		return new RemoteRenderingToolBarContent();
	}
}
