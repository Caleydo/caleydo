package org.caleydo.core.view.opengl.layout.event;

import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for classes that want to handle collisions in the layouts
 * 
 * @author Alexander Lex
 */
public interface ILayoutSizeCollisionHandler
	extends IListenerOwner {

	/**
	 * Handle a size collision in a layout
	 * 
	 * @param managingClassID
	 *            the identifier for the class managing the calling layout
	 * @param layoutID
	 *            the id of the layout (arbitrarily chose by the managing class)
	 * @param toBigBy
	 *            how much the layouts are to big by
	 */
	public void handleLayoutSizeCollision(int managingClassID, int layoutID, float toBigBy);

}
