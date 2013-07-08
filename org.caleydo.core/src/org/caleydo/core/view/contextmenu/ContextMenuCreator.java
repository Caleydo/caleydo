/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLView;

import com.google.common.collect.Iterators;

public class ContextMenuCreator implements Iterable<AContextMenuItem> {
	private final List<AContextMenuItem> menuItems = new ArrayList<>();

	/**
	 *
	 */
	public ContextMenuCreator() {
	}

	public synchronized void clear() {
		menuItems.clear();
	}

	public synchronized void addSeparator() {
		menuItems.add(SeparatorMenuItem.INSTANCE);
	}

	public synchronized void addContextMenuItem(AContextMenuItem menuItem) {
		menuItems.add(menuItem);
	}

	public void add(AContextMenuItem item) {
		addContextMenuItem(item);
	}

	public void add(String label, AEvent event) {
		addContextMenuItem(new GenericContextMenuItem(label, event));
	}

	public void addAll(Iterable<Pair<String, ? extends AEvent>> events) {
		for (Pair<String, ? extends AEvent> event : events)
			add(event.getFirst(), event.getSecond());
	}

	public synchronized void addContextMenuItemContainer(AContextMenuItemContainer menuItemContainer) {
		menuItems.addAll(menuItemContainer.getContextMenuItems());
	}

	public synchronized boolean hasMenuItems() {
		return !menuItems.isEmpty();
	}

	public synchronized void open(final IGLView view) {
		view.getParentGLCanvas().showPopupMenu(menuItems);
	}

	@Override
	public Iterator<AContextMenuItem> iterator() {
		return Iterators.unmodifiableIterator(menuItems.iterator());
	}

}
