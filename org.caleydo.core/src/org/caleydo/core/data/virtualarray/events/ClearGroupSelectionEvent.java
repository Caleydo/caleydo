/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.event.AEvent;

/**
 * Clears a group selection of enRoute
 *
 * @author Alexander Lex
 *
 */
public class ClearGroupSelectionEvent extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
