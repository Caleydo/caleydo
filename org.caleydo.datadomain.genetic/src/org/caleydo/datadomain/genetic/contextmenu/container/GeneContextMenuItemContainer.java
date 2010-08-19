package org.caleydo.datadomain.genetic.contextmenu.container;

import java.util.Set;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.genetic.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.datadomain.genetic.contextmenu.item.ShowPathwaysByGeneItem;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Implementation of AItemContainer for Genes. By passing a RefSeq int code all
 * relevant context menu items are constructed automatically
 * 
 * @author Alexander Lex
 */
public class GeneContextMenuItemContainer extends AItemContainer {

	/**
	 * Constructor.
	 */
	public GeneContextMenuItemContainer() {
		super();

	}

	public void setID(IDType idType, int id) {
		Set<Integer> davids = GeneralManager.get().getIDMappingManager()
				.getID(idType, dataDomain.getPrimaryContentMappingType(), id);
		if (davids == null)
			return;
		for (Integer david : davids) {
			createMenuContent(david);
//			return;
		}
	}

	private void createMenuContent(int davidID) {
		GeneralManager generalManager = GeneralManager.get();

		String sGeneSymbol = generalManager.getIDMappingManager().getID(
				dataDomain.getPrimaryContentMappingType(),
				((GeneticDataDomain) (DataDomainManager.getInstance()
						.getDataDomain(dataDomain.getDataDomainType())))
						.getHumanReadableContentIDType(), davidID);
		if (sGeneSymbol == "" || sGeneSymbol == null)
			sGeneSymbol = "Unkonwn Gene";
		addHeading(sGeneSymbol);

		if (PathwayManager.get().isPathwayLoadingFinished()) {
			LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
			loadPathwaysByGeneItem.setDavid(dataDomain.getPrimaryContentMappingType(),
					davidID);
			addContextMenuItem(loadPathwaysByGeneItem);

			ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
			showPathwaysByGeneItem.setDavid(dataDomain.getPrimaryContentMappingType(),
					davidID);
			addContextMenuItem(showPathwaysByGeneItem);
		}

//		BookmarkItem addToListItem = new BookmarkItem(
//				dataDomain.getPrimaryContentMappingType(), davidID);

//		addContextMenuItem(addToListItem);
	}
}
