/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.IListenerOwner;

public interface IResettableView
	extends IListenerOwner {

	/**
	 * Reset the view to its initial state
	 */
	public void resetView();
}
