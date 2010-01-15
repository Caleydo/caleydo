package org.caleydo.core.manager.view.creator;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

public abstract class AGLViewCreator
	implements IGLViewCreator {

	private String viewType;
	
	public AGLViewCreator(String viewType) {
		this.viewType = viewType;
	}
	
	@Override
	public abstract AGLView createGLEventListener(ECommandType type, GLCaleydoCanvas glCanvas, String label,
		IViewFrustum viewFrustum);

	@Override
	public String getViewType() {
		return viewType;
	}

}
