package org.caleydo.view.scatterplot.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.scatterplot.GLScatterPlot;
import org.caleydo.view.scatterplot.SerializedScatterplotView;
import org.caleydo.view.scatterplot.toolbar.ScatterplotToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLScatterPlot(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedScatterplotView();
	}

	@Override
	public Object createToolBarContent() {
		return new ScatterplotToolBarContent();
	}
}
