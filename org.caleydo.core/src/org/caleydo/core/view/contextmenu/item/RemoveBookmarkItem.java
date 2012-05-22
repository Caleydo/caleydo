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
package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.event.data.RemoveBookmarkEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * Item that adds a selected element to the bookmark container
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class RemoveBookmarkItem
	extends AContextMenuItem {

	/**
	 * Constructor which takes a single dimension index.
	 */
	public RemoveBookmarkItem(String label, IDType idType, int id) {
		setLabel(label);

		RemoveBookmarkEvent<Integer> event = new RemoveBookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(this);
		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of dimension indices.
	 */
	public RemoveBookmarkItem(String label, IDType idType, ArrayList<Integer> ids) {
		setLabel(label);

		RemoveBookmarkEvent<Integer> event = new RemoveBookmarkEvent<Integer>(idType);
		event.setSender(this);
		for (Integer id : ids)
			event.addBookmark(id);
		registerEvent(event);
	}
}
