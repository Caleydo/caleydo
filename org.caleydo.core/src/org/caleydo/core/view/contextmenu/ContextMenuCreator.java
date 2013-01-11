/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class ContextMenuCreator {
	private static final SeparatorMenuItem SEPARATOR = new SeparatorMenuItem();
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
		menuItems.add(SEPARATOR);
	}

	public synchronized void addContextMenuItem(AContextMenuItem menuItem) {
		menuItems.add(menuItem);
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

	public synchronized void open(final AGLView view) {
		ViewManager.get().getCanvasFactory().showPopupMenu(view, menuItems);
	}

}
