/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.virtualarray.events.VADeltaListener;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for view classes that need to get information on when to update a view. Implementation of this
 * interface are called by {@link VADeltaListener}s.
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

}
