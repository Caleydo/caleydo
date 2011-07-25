package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;

/**
 * Implementation of AItemContainer for Experiments. You need to pass an ID of the Gene Category, which has
 * the datatype int.
 * 
 * @author Alexander Lex
 */
public class DimensionContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public DimensionContextMenuItemContainer() {
		super();
	}

	/**
	 * Set the experiment index
	 */
	public void dataTableID(IDType idType, int experimentIndex) {
		createMenuContent(idType, experimentIndex);
	}

	private void createMenuContent(IDType idType, int experimentIndex) {
		String sExperimentTitle = dataDomain.getDimensionLabel(idType, experimentIndex);

		addHeading(sExperimentTitle);

		BookmarkItem addToListItem = new BookmarkItem(idType, experimentIndex);
		addContextMenuItem(addToListItem);
	}
}
