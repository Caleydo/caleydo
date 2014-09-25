/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.scrollbar;

/**
 * Implementors of this interface are notified when ScrollBars they registered for are updated.
 * 
 * @author Partl
 */
public interface IScrollBarUpdateHandler {

	/**
	 * This method is called, when a ScrollBar was updated (e.g. dragged by a user).
	 * 
	 * @param scrollBar
	 *            The ScrollBar that was updated.
	 */
	public void handleScrollBarUpdate(ScrollBar scrollBar);

}
