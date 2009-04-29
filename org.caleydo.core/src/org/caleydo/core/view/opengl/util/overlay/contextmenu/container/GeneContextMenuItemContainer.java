package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.AddToListItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.ShowPathwaysByGeneItem;

/**
 * Implementation of AItemContainer for Genes. By passing a RefSeq int code all relevant context menu items
 * are constructed automatically
 * 
 * @author Alexander Lex
 */
public class GeneContextMenuItemContainer
	extends AItemContainer {
	/**
	 * Constructor that takes a refSeq Integer representation and creates all context menu items that are
	 * relevant for genes.
	 * 
	 * @param refSeqInt
	 *            a refSeq int representation
	 */
	public GeneContextMenuItemContainer(int refSeqInt) {
		super();

		LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
		loadPathwaysByGeneItem.setRefSeqInt(refSeqInt);
		addContextMenuItem(loadPathwaysByGeneItem);

		ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
		showPathwaysByGeneItem.setRefSeqInt(refSeqInt);
		addContextMenuItem(showPathwaysByGeneItem);

		AddToListItem addToListItem = new AddToListItem();
		addContextMenuItem(addToListItem);

	}

}
