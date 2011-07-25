package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.tablebased.UpdateViewEvent;

/**
 * Listener that signals that a major update of a view is required. In contrast to {@Re This listener
 * calls a related {@link IViewCommandHandler}.
 * 
 * @author Werner Puff
 */
public class UpdateViewListener
	extends AEventListener<IViewCommandHandler> {

	/**
	 * Handles {@link RedrawViewEvent}s calling the related handler
	 * 
	 * @param event
	 *            {@link RedrawViewEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateViewEvent) {
			handler.handleUpdateView();
		}
	}

}
