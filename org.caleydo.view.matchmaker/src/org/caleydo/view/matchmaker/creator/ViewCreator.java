package org.caleydo.view.matchmaker.creator;

import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.SerializedMatchmakerView;
import org.caleydo.view.matchmaker.toolbar.MatchmakerToolBarContent;

public class ViewCreator extends AGLViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AGLView createGLView(GLCaleydoCanvas glCanvas, String label,
			IViewFrustum viewFrustum) {

		return new GLMatchmaker(glCanvas, label, viewFrustum);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedMatchmakerView();
	}

	@Override
	public Object createToolBarContent() {
		return new MatchmakerToolBarContent();
	}
}
