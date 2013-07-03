/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class NewFilterListener extends AEventListener<FilterManager> {


	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewFilterEvent) {
			handler.addFilter(((NewFilterEvent) event).getFilter());
		}
	}
}
