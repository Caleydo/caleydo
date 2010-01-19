package org.caleydo.core.manager.view.creator;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

public abstract class AGLViewCreator
	extends AViewCreator {

	public AGLViewCreator(String viewType) {
		super(viewType);
	}

	public abstract AGLView createGLView(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum);
}
