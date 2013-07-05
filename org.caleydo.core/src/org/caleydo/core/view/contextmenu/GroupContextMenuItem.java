/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;

/**
 * generic implementation of a context menu item
 *
 * @author Samuel Gratzl
 *
 */
public class GroupContextMenuItem extends AContextMenuItem {
	public GroupContextMenuItem(String label) {
		setLabel(label);
	}

	public void add(AContextMenuItem item) {
		super.addSubItem(item);
	}

	public void add(String label, AEvent event) {
		add(new GenericContextMenuItem(label, event));
	}

	public void addAll(Iterable<Pair<String, ? extends AEvent>> events) {
		for (Pair<String, ? extends AEvent> event : events)
			add(event.getFirst(), event.getSecond());
	}
}
