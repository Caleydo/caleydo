package org.caleydo.view.bookmark.contextmenu;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.RemoveBookmarkItem;

/**
 * Implementation of AItemContainer for elements in the bookmark menue. You need to pass an ID and an ID type.
 * 
 * @author Alexander Lex
 */
public class BookmarkContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public BookmarkContextMenuItemContainer() {
		super();
	}

	/**
	 * Set the experiment index
	 */
	public void setID(IDType idType, int id) {
		createMenuContent(idType, id);
	}

	private void createMenuContent(IDType idType, int id) {

		if (dataDomain != null) {
			addItemContainer(dataDomain.getContentItemContainer(idType, id));
		}

		RemoveBookmarkItem removeBookmarkItem = new RemoveBookmarkItem(idType, id);
		addContextMenuItem(removeBookmarkItem);

	}
}
