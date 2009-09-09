package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.UpdateGroupInfoEvent;

/**
 * Listener for update group information. This listener calls a related {@link IViewCommandHandler}.
 * 
 * @author Bernhard Schlegl
 */
public class UpdateGroupInfoListener
	extends AEventListener<IViewCommandHandler> {

	/**
	 * Handles {@link UpdateGroupInfoEvent}s calling the related handler
	 * 
	 * @param event
	 *            {@link UpdateGroupInfoEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateGroupInfoEvent) {
			handler.handleUpdateGroupInfo();
		}
	}

}
