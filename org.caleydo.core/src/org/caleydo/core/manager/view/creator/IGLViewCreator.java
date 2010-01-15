package org.caleydo.core.manager.view.creator;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

public interface IGLViewCreator {
	
	public AGLView createGLEventListener(ECommandType type, GLCaleydoCanvas glCanvas,
		final String label, final IViewFrustum viewFrustum);
	
	public String getViewType();
}
