/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.util.collection.Pair;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectGroupEvent extends ADirectedEvent {
	private final Predicate<Pair<TablePerspective, Group>> filter;

	public SelectGroupEvent(Predicate<Pair<TablePerspective, Group>> filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public Predicate<Pair<TablePerspective, Group>> getFilter() {
		return filter;
	}


	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
