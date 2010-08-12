package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;

/**
 * Implementation of AItemContainer for Genes. By passing a RefSeq int code all relevant context menu items
 * are constructed automatically
 * 
 * @author Alexander Lex
 */
public class ContentContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public ContentContextMenuItemContainer() {
		super();

	}

	public void setID(IDType idType, int id) {
		// Integer davidID = GeneralManager.get().getIDMappingManager().getID(idType,
		// GeneticDataDomain.centralIDType, id);
		// if (davidID == null)
		// return;
		createMenuContent(idType, id);
	}

	private void createMenuContent(IDType idType, int id) {

		if (dataDomain != null) {
			AItemContainer subContainer = dataDomain.getContentItemContainer(idType, id);
			if (subContainer != null)
				addItemContainer(subContainer);
		}

		BookmarkItem addToListItem = new BookmarkItem(idType, id);

		addContextMenuItem(addToListItem);
	}
}
