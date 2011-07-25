package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;

/**
 * Implementation of AItemContainer for Content Items. Dynamically adds dataDomain specific content Context
 * menu if a dataDomain is specified.
 * 
 * @author Alexander Lex
 */
public class RecordContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public RecordContextMenuItemContainer() {
		super();

	}

	public void dataTableID(IDType idType, int id) {
		createMenuContent(idType, id);
	}

	private void createMenuContent(IDType idType, int id) {

		if (dataDomain != null) {
			AItemContainer subContainer = dataDomain.getRecordItemContainer(idType, id);
			if (subContainer != null)
				addItemContainer(subContainer);
		}

		BookmarkItem addToListItem = new BookmarkItem(idType, id);

		addContextMenuItem(addToListItem);
	}
}
