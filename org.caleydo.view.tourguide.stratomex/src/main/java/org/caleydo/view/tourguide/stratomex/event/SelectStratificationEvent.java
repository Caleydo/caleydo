/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectStratificationEvent extends ADirectedEvent {
	private final Predicate<TablePerspective> filter;

	public SelectStratificationEvent(Predicate<TablePerspective> filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public Predicate<TablePerspective> getFilter() {
		return filter;
	}


	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
