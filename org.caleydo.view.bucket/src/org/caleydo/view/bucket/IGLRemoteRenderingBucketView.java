package org.caleydo.view.bucket;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

public interface IGLRemoteRenderingBucketView extends IGLRemoteRenderingView {

	/**
	 * Returns the center layer of the bucket.
	 */
	public RemoteLevel getFocusLevel();

	public BucketMouseWheelListener getBucketMouseWheelListener();

	/**
	 * Adds a view to the initially contained remote rendered views
	 * 
	 * @param view
	 *            serialized form of the view to add
	 */
	public void addInitialRemoteView(ASerializedView view);
}
