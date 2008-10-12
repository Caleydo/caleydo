package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketMouseWheelListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;

/**
 * Interface for accessing views that remotely render other views.
 * 
 * @author Marc Streit
 */
public interface IGLCanvasRemoteRendering3D
{

	public RemoteHierarchyLevel getHierarchyLayerByGLEventListenerId(
			final int iGLEventListenerId);

	/**
	 * Returns the center layer of the bucket.
	 */
	public RemoteHierarchyLevel getUnderInteractionHierarchyLayer();

	public BucketMouseWheelListener getBucketMouseWheelListener();
}
