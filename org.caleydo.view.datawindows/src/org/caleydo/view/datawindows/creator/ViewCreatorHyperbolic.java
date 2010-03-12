package org.caleydo.view.datawindows.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.datawindows.GLHyperbolic;
import org.caleydo.view.datawindows.SerializedHyperbolicView;

public class ViewCreatorHyperbolic extends AGLViewCreator {

	public ViewCreatorHyperbolic(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLHyperbolic(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHyperbolicView();
	}

	@Override
	public Object createToolBarContent() {
		return null;
	}
}
