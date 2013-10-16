/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * marker event that a job died
 * 
 * @author Samuel Gratzl
 * 
 */
public class JobDiedEvent extends ADirectedEvent {
	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
