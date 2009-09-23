package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;
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
	 * Constructor.
	 */
	public GeneContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) == null)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	public void setID(EIDType idType, int id) {
		Integer davidID = GeneralManager.get().getIDMappingManager().getID(idType, EIDType.DAVID, id);
		if (davidID == null)
			return;
		createMenuContent(davidID);
	}

	private void createMenuContent(int davidID) {
		String sGeneSymbol =
			GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID, EIDType.GENE_SYMBOL, davidID);
		if (sGeneSymbol == "" || sGeneSymbol == null)
			sGeneSymbol = "Unkonwn Gene";
		addHeading(sGeneSymbol);

		if (GeneralManager.get().getPathwayManager().isPathwayLoadingFinished()) {
			LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
			loadPathwaysByGeneItem.setDavid(davidID);
			addContextMenuItem(loadPathwaysByGeneItem);

			ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
			showPathwaysByGeneItem.setDavid(davidID);
			addContextMenuItem(showPathwaysByGeneItem);
		}

		BookmarkItem addToListItem = new BookmarkItem(EIDType.DAVID, davidID);

		addContextMenuItem(addToListItem);
	}
}
