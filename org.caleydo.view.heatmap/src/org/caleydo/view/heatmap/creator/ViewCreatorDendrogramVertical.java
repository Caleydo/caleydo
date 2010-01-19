package org.caleydo.view.heatmap.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.GLDendrogram;
import org.caleydo.view.heatmap.SerializedDendogramVerticalView;

public class ViewCreatorDendrogramVertical extends AGLViewCreator {

	public ViewCreatorDendrogramVertical(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLDendrogram(glCanvas, label, viewFrustum, false);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedDendogramVerticalView();
	}
}
