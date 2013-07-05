/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;

/**
 * Event that signals that an existing perspective was updated.
 *
 * @author Alexander Lex
 *
 */
public class PerspectiveUpdatedEvent extends AEvent {

	Perspective perspective;

	/**
	 *
	 */
	public PerspectiveUpdatedEvent(Perspective perspective) {
		this.perspective = perspective;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public Perspective getPerspective() {
		return perspective;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspective != null)
			return true;
		return false;
	}

}
