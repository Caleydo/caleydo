package org.caleydo.core.view.opengl.util.vislink;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.util.vislink.VisLinkEnvironment;

public class VLSelectionUpdateListener
extends AEventListener<ISelectionUpdateHandler> {

	/**
	 * Listener to set animation start time
	 * @param event {@link SelectionUpdateEvent} to handle, other events will be ignored 
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionUpdateEvent)
			VisLinkEnvironment.resetAnimation(System.currentTimeMillis());

	}

}
