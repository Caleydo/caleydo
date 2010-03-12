package org.caleydo.view.datawindows.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.datawindows.GLDataWindows;
import org.caleydo.view.datawindows.SerializedDataWindowsView;

public class ViewCreatorDataWindows extends AGLViewCreator {

	public ViewCreatorDataWindows(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLDataWindows(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedDataWindowsView();
	}

	@Override
	public Object createToolBarContent() {
		return null;
	}
}
