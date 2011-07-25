package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item that adds a selected element to the bookmark container
 * 
 * @author Alexander Lex
 */
public class RemoveBookmarkItem
	extends AContextMenuItem {

	/**
	 * Constructor which takes a single dimension index.
	 * 
	 * @param iDimensionIndex
	 */
	public RemoveBookmarkItem(IDType idType, int id) {
		super();
		setIconTexture(EIconTextures.REMOVE);
		setText("Delete");
		RemoveBookmarkEvent<Integer> event = new RemoveBookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(this);
		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of dimension indices.
	 * 
	 * @param alDimensionIndex
	 */
	public RemoveBookmarkItem(IDType idType, ArrayList<Integer> ids) {
		super();
		setIconTexture(EIconTextures.CM_BOOKMARK);
		setText("Bookmark");
		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);

		for (Integer id : ids)
			event.addBookmark(id);
		registerEvent(event);
	}

}
