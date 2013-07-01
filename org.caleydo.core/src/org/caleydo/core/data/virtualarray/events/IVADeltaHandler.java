/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.event.IListenerOwner;

/**
 * Handler interface for listeners for {@link VADeltaEvent} and {@link Repl}.
 * 
 * @author Alexander Lex
 */
public interface IVADeltaHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is caught by a related {@link VADeltaListener}.
	 * 
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleVADelta(VirtualArrayDelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplacePerspectiveListener} when a {@link ReplacePerspectiveEvent} was
	 * received.
	 */
	public void replacePerspective(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data);

}
