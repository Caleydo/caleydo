package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.util.mapping.color.ColorMapper;

/**
 * Interface for view and manager that hold {@link ColorMapper}s.
 * 
 * @author Werner Puff
 */
public interface IColorMappingHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a {@link ColorMapper} has been changed and the related
	 * {@link UpdateColorMappingEvent} is caught by a {@link UpdateColorMappingListener}.
	 */
	public void distributeColorMapping(ColorMapper colorMapping);
}
