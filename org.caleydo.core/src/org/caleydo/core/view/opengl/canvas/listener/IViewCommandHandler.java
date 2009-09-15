package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for view classes that need to get information on when to update a view. Implementation of this
 * interface are called by {@link VirtualArrayUpdateListener}s.
 * 
 * @author Werner Puff
 */
public interface IViewCommandHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a redraw view update event is caught by a related
	 * {@link RedrawViewListener}.
	 */
	public void handleRedrawView();

	/**
	 * Handler method to be called when a major view update is required. It has to be called by a
	 * {@link UpdateViewListener}.
	 */
	public void handleUpdateView();

	/**
	 * Handler method to be called when a clear selections view update event is is caught by a related
	 * {@link ClearSelectionsListener}.
	 */
	public void handleClearSelections();

}
