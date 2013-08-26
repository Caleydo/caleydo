/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * special event to trigger that all leading scores should be removed
 * 
 * @author Samuel Gratzl
 * 
 */
public class RemoveLeadingScoreColumnsEvent extends ADirectedEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}

