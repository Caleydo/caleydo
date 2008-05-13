package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Interface for accessing views that remotely render other views.
 * 
 * @author Marc Streit
 *
 */
public interface IGLCanvasRemoteRendering3D 
{
	public JukeboxHierarchyLayer getHierarchyLayerByGLCanvasListenerId(
			final int iGLCanvasListenerId);
}
