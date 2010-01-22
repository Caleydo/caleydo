package org.caleydo.view.heatmap.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.SerializedHeatMapView;
import org.caleydo.view.heatmap.toolbar.HeatMapToolBarContent;

public class ViewCreatorHeatMap extends AGLViewCreator {

	public ViewCreatorHeatMap(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLHeatMap(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHeatMapView();
	}

	@Override
	public Object createToolBarContent() {
		return new HeatMapToolBarContent();
	}
}
