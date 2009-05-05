package org.caleydo.core.view.opengl.canvas.listener;


/**
 * Interface for views classes that needs to get 
 * information update the need to update a view. 
 * Implementation of this interface are called 
 * by {@link VirtualArrayUpdateListener}s.
 *  
 * @author Werner Puff
 */
public interface IViewCommandHandler {
	
	/**
	 * Handler method to be called when a redraw view update event is is catched 
	 * by a related {@link RedrawViewListener}.
	 */
	public void handleRedrawView();

	
	/**
	 * Handler method to be called when a clear selections view update event is is catched 
	 * by a related {@link ClearSelectionsListener}.
	 */
	public void handleClearSelections();
	
}
