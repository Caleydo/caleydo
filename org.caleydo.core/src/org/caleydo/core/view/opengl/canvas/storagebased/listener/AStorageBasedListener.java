package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;

/**
 * abstract base class for listeners related to {@link AStorageBasedView}
 * @author Werner Puff
 */
public abstract class AStorageBasedListener
	implements IEventListener {

	/** view this listener is related to */
	protected AStorageBasedView view = null;

	public AStorageBasedView getView() {
		return view;
	}

	public void setView(AStorageBasedView view) {
		this.view = view;
	}

}
