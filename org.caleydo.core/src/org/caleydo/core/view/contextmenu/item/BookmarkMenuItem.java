package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.data.BookmarkEvent;
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
	public BookmarkMenuItem(String label, IDType idType, int id, String dataDomainID) {
		setLabel(label);

		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of dimension indices.
	 */
	public BookmarkMenuItem(String label, IDType idType, ArrayList<Integer> ids, String dataDomainID) {
		setLabel(label);

		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		for (Integer id : ids)
			event.addBookmark(id);
		registerEvent(event);
	}
}
