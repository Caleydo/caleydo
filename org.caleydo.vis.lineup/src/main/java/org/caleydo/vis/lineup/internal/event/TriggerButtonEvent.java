/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;


public class TriggerButtonEvent extends ADirectedEvent {
	private GLButton button;

	public TriggerButtonEvent(GLButton button) {
		this.button = button;
	}

	public GLButton getButton() {
		return button;
	}
	@Override
	public boolean checkIntegrity() {
		return button != null;
	}
}
