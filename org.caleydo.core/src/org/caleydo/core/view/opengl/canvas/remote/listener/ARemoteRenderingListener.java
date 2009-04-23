package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

/**
 * abstract base class for listeners related to the remote rendering view
 * @author Werner Puff
 */
public abstract class ARemoteRenderingListener
	implements IEventListener {

	/** remote rendering view this listener is related to */
	protected GLRemoteRendering bucket = null;
	
	public GLRemoteRendering getBucket() {
		return bucket;
	}

	public void setBucket(GLRemoteRendering bucket) {
		this.bucket = bucket;
	}

}
