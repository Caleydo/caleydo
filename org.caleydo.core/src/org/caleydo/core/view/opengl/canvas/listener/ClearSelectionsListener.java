package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;

/**
 * Listener for clear selection events. This listener calls a related {@link IViewCommandHandler}.
 * 
 * @author Werner Puff
 */
public class ClearSelectionsListener
	extends AEventListener<IViewCommandHandler> {

	/**
	 * Handles {@link ClearSelectionsEvent}s calling the related handler
	 * 
	 * @param event
	 *            {@link RedrawViewEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearSelectionsEvent) {
			handler.handleClearSelections();
		}
	}

}
