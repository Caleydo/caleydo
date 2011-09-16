package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.IListenerOwner;

public interface IResettableView
	extends IListenerOwner {

	/**
	 * Reset the view to its initial state
	 */
	public void resetView();
}
