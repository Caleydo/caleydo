/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.event;

import org.caleydo.core.event.AEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class WizardEndedEvent extends AEvent {
	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

