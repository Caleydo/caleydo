package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.view.opengl.canvas.histogram.listener.UpdateColorMappingListener;

/**
 * Interface for view and manager that hold {@link ColorMapping}s.
 * 
 * @author Werner Puff
 */
public interface IColorMappingHandler extends IListenerOwner {

	/**
	 * Handler method to be called when a {@link ColorMapping} has been changed and the related
	 * {@link UpdateColorMappingEvent} is caught by a {@link UpdateColorMappingListener}.
	 */
	public void distributeColorMapping(ColorMapping colorMapping);
}
