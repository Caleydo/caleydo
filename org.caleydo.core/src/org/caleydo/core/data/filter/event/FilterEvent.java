/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 */
public class FilterEvent
	extends AEvent {

	private Filter filter = null;

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	@Override
	public boolean checkIntegrity() {
		if (filter == null)
			return false;

		return true;
	}
}
