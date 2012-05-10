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
import java.util.Iterator;
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
public abstract class AContextMenuItemContainer
	implements Iterable<AContextMenuItem> {

	private ArrayList<AContextMenuItem> contextMenuEntries;

	protected ATableBasedDataDomain dataDomain = null;

	/**
	 * Constructor
	 */
	public AContextMenuItemContainer() {
		contextMenuEntries = new ArrayList<AContextMenuItem>();
	}

	/**
	 * Adds a context menu item to the container
	 * 
	 * @param contextMenuItem
	 */
	public void addContextMenuItem(AContextMenuItem contextMenuItem) {
		contextMenuEntries.add(contextMenuItem);
	}

	public void addItemContainer(AContextMenuItemContainer container) {
		for (AContextMenuItem item : container) {
			contextMenuEntries.add(item);
		}
	}

	/**
	 * Adds a separator to the context menu
	 */
	public void addSeparator() {
		contextMenuEntries.add(new SeparatorMenuItem());
	}

	/**
	 * Returns the list of context menu items
	 * 
	 * @return
	 */
	public ArrayList<AContextMenuItem> getContextMenuItems() {
		return contextMenuEntries;
	}

	@Override
	public Iterator<AContextMenuItem> iterator() {
		return contextMenuEntries.iterator();
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
}
