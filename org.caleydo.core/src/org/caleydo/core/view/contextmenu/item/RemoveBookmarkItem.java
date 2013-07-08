/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
