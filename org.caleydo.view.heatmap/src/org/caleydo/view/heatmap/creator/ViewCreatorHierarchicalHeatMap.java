package org.caleydo.view.heatmap.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.heatmap.GLHierarchicalHeatMap;
import org.caleydo.view.heatmap.SerializedHierarchicalHeatMapView;
import org.caleydo.view.heatmap.toolbar.HierarchicalHeatMapToolBarContent;

public class ViewCreatorHierarchicalHeatMap extends AGLViewCreator {

	public ViewCreatorHierarchicalHeatMap(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLHierarchicalHeatMap(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHierarchicalHeatMapView();
	}
	
	@Override
	public Object createToolBarContent() {
		return new HierarchicalHeatMapToolBarContent();
	}
}
