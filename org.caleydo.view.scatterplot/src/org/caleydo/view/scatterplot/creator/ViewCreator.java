package org.caleydo.view.scatterplot.creator;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.scatterplot.GLScatterplot;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLEventListener(ECommandType type,
			GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {

		return new GLScatterplot(glCanvas, label, viewFrustum);
	}

}
