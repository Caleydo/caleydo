package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

/**
 * Interface for accessing views that remotely render other views.
 * 
 * @author Marc Streit
 */
public interface IGLRemoteRenderingView extends IUniqueObject {
	
	public GLCaleydoCanvas getParentGLCanvas();
}
