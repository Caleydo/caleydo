package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketMouseWheelListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Interface for accessing views that remotely render other views.
 * 
 * @author Marc Streit
 */
public interface IGLCanvasRemoteRendering3D
{

	public RemoteHierarchyLayer getHierarchyLayerByGLEventListenerId(
			final int iGLEventListenerId);

	public BucketMouseWheelListener getBucketMouseWheelListener();
}
