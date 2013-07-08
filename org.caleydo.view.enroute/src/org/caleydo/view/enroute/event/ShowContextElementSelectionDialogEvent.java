/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.event;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 *
 */
public class ShowContextElementSelectionDialogEvent extends AEvent {

	Perspective perspective;

	/**
	 *
	 */
	public ShowContextElementSelectionDialogEvent(Perspective perspective) {
		this.perspective = perspective;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public Perspective getPerspective() {
		return perspective;
	}

	/**
	 * @param perspective
	 *            setter, see {@link perspective}
	 */
	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspective != null)
			return true;

		return false;
	}

}
