package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item that adds a selected element to the bookmark container
 * 
 * @author Alexander Lex
 */
public class BookmarkItem
	extends AContextMenuItem {

	/**
	 * Constructor which takes a single storage index.
	 * 
	 * @param iStorageIndex
	 */
	public BookmarkItem(IDType idType, int id) {
		super();
		setIconTexture(EIconTextures.CM_BOOKMARK);
		setText("Bookmark");

		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(this);
		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of storage indices.
	 * 
	 * @param alStorageIndex
	 */
	public BookmarkItem(IDType idType, ArrayList<Integer> ids) {
		super();
		setIconTexture(EIconTextures.CM_BOOKMARK);
		setText("Bookmark");
		
		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.setSender(this);

		for (Integer id : ids)
			event.addBookmark(id);
		registerEvent(event);
	}

}
