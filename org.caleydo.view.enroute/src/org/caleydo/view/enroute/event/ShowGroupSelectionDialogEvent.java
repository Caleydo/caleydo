/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.event;

import java.util.List;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 *
 */
public class ShowGroupSelectionDialogEvent extends AEvent {

	List<Perspective> perspectives;

	/**
	 *
	 */
	public ShowGroupSelectionDialogEvent(List<Perspective> perspectives) {
		this.perspectives = perspectives;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public List<Perspective> getPerspectives() {
		return perspectives;
	}

	/**
	 * @param perspective
	 *            setter, see {@link perspective}
	 */
	public void setPerspective(List<Perspective> perspectives) {
		this.perspectives = perspectives;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspectives != null)
			return true;

		return false;
	}

}
