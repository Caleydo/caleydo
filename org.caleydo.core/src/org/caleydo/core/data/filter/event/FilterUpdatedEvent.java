/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.event;

import org.caleydo.core.event.AEvent;

/**
 * Signal that some property of the filters requiring adaption of a visualization of the filters was changed
 * 
 * @author Alexander Lex
 */
public class FilterUpdatedEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
