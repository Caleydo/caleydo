package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.util.mapping.color.ColorMapper;

/**
 * Listener for updates on {@link ColorMapper}s.
 * 
 * @author Werner Puff
 */
public class UpdateColorMappingListener
	extends AEventListener<IColorMappingHandler> {

	/**
	 * Handles {@link ClearSelectionsEvent}s calling the related handler
	 * 
	 * @param event
	 *            {@link RedrawViewEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateColorMappingEvent) {
			UpdateColorMappingEvent updateColorMappingEvent = (UpdateColorMappingEvent) event;
			handler.distributeColorMapping(updateColorMappingEvent.getColorMapping());
		}
	}

}
