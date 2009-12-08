package org.caleydo.core.view.opengl.canvas.histogram.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.view.opengl.canvas.listener.IColorMappingHandler;

/**
 * Listener for updates on {@link ColorMapping}s.
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
