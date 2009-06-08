package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IResettableView
	extends IListenerOwner {

	/**
	 * Reset the view to its initial state
	 */
	public void resetView();
}
