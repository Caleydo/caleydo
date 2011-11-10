package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;

/**
 * Listener for {@link RedrawViewEvent}s.
 * 
 * @author Werner Puff
 */
public class RedrawViewListener
	extends AEventListener<IViewCommandHandler> {

	/**
	 * Handles {@link RedrawViewEvent}s calling the related handler
	 * 
	 * @param event
	 *            {@link RedrawViewEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RedrawViewEvent) {
			handler.handleRedrawView();
		}
	}
}
