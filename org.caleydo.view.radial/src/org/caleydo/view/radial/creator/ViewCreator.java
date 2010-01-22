package org.caleydo.view.radial.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.radial.GLRadialHierarchy;
import org.caleydo.view.radial.SerializedRadialHierarchyView;
import org.caleydo.view.radial.toolbar.RadialHierarchyToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLRadialHierarchy(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedRadialHierarchyView();
	}

	@Override
	public Object createToolBarContent() {
		return new RadialHierarchyToolBarContent();
	}
}
