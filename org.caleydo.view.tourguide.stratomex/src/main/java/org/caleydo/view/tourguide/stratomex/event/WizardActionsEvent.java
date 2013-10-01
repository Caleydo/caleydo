/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class WizardActionsEvent extends ADirectedEvent {
	private final String pickingType;

	public WizardActionsEvent(String pickingType) {
		this.pickingType = pickingType;
	}

	public String getPickingType() {
		return pickingType;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}

