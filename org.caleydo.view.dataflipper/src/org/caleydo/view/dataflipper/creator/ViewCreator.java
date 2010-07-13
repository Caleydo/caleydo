package org.caleydo.view.dataflipper.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.dataflipper.GLDataFlipper;
import org.caleydo.view.dataflipper.SerializedDataFlipperView;
import org.caleydo.view.dataflipper.toolbar.DataFlipperToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLDataFlipper(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedDataFlipperView();
	}
	
	@Override
	public Object createToolBarContent() {
		return new DataFlipperToolBarContent();
	}
}