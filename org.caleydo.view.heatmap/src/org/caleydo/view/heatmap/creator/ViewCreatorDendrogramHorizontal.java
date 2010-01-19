package org.caleydo.view.heatmap.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.GLDendrogram;
import org.caleydo.view.heatmap.SerializedDendogramHorizontalView;

public class ViewCreatorDendrogramHorizontal extends AGLViewCreator {

	public ViewCreatorDendrogramHorizontal(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLDendrogram(glCanvas, label, viewFrustum, true);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedDendogramHorizontalView();
	}
}
