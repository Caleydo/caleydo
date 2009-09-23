package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
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
	public void setID(EIDType idType, int id) {
		createMenuContent(idType, id);
	}

	private void createMenuContent(EIDType idType, int id) {

		if (idType.getCategory() == EIDCategory.GENE) {
			GeneContextMenuItemContainer geneContainer = new GeneContextMenuItemContainer();
			geneContainer.setID(EIDType.DAVID, id);
			addItemContainer(geneContainer);
			addSeparator();
		}

		RemoveBookmarkItem removeBookmarkItem = new RemoveBookmarkItem(idType, id);
		addContextMenuItem(removeBookmarkItem);

	}
}
