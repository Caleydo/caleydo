/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * Item that adds a selected element to the bookmark container
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class BookmarkMenuItem
	extends AContextMenuItem {

	/**
	 * Constructor which takes a single dimension index.
	 */
	public BookmarkMenuItem(String label, IDType idType, int id) {
		setLabel(label);

		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(this);

		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of dimension indices.
	 */
	public BookmarkMenuItem(String label, IDType idType, ArrayList<Integer> ids, String dataDomainID) {
		setLabel(label);

		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.setSender(this);
		event.setEventSpace(dataDomainID);
		for (Integer id : ids)
			event.addBookmark(id);
		registerEvent(event);
	}
}
