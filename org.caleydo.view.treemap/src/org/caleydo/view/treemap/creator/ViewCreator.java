package org.caleydo.view.treemap.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.treemap.GLTreeMap;
import org.caleydo.view.treemap.SerializedTreeMapView;
import org.caleydo.view.treemap.toolbar.TreeMapToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLTreeMap(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTreeMapView();
	}

	@Override
	public Object createToolBarContent() {
		return new TreeMapToolBarContent();
	}
}
