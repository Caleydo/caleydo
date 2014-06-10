/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;

/**
 * Abstract implementation for a container for items grouped by a specific topic. It holds a list of
 * AContextMenuItem which should be specified by inheriting classes. Subclasses of AItemContainer are intended
 * to be constructed out of one single point of references (for example a single ID). They can be passed to
 * the {@link ContextMenu} as a whole.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AContextMenuItemContainer implements Iterable<AContextMenuItem> {

	private List<AContextMenuItem> entries = new ArrayList<>();

	protected ATableBasedDataDomain dataDomain = null;

	/**
	 * Adds a context menu item to the container
	 *
	 * @param contextMenuItem
	 */
	public void addContextMenuItem(AContextMenuItem contextMenuItem) {
		entries.add(contextMenuItem);
	}

	public void addItemContainer(AContextMenuItemContainer container) {
		for (AContextMenuItem item : container) {
			entries.add(item);
		}
	}

	/**
	 * Adds a separator to the context menu
	 */
	public void addSeparator() {
		entries.add(SeparatorMenuItem.INSTANCE);
	}

	/**
	 * Returns the list of context menu items
	 *
	 * @return
	 */
	public List<AContextMenuItem> getContextMenuItems() {
		return entries;
	}

	@Override
	public Iterator<AContextMenuItem> iterator() {
		return entries.iterator();
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
}
