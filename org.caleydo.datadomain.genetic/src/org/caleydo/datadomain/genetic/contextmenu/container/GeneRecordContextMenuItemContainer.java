package org.caleydo.datadomain.genetic.contextmenu.container;

import java.util.Set;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.AContextMenuItemContainer;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.datadomain.genetic.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.datadomain.genetic.contextmenu.item.ShowPathwaysByGeneItem;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Implementation of AItemContainer for Genes. By passing an ID all
 * relevant context menu items are constructed automatically
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GeneRecordContextMenuItemContainer extends AContextMenuItemContainer {

	public void setData(IDType idType, int id) {
		
		AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark Gene: "
				+ dataDomain.getRecordLabel(idType, id),
				idType, id, dataDomain.getDataDomainID());
		addContextMenuItem(menuItem);
		
		Set<Integer> davids = GeneralManager.get().getIDMappingManager()
				.getIDAsSet(idType, dataDomain.getPrimaryRecordMappingType(), id);
		if (davids == null)
			return;
		for (Integer david : davids) {
			createMenuContent(david);
		}
	}

	private void createMenuContent(int davidID) {
		
//		String sGeneSymbol = generalManager.getIDMappingManager().getID(
//				dataDomain.getPrimaryRecordMappingType(),
//				((GeneticDataDomain) (DataDomainManager.get().getDataDomainByID(dataDomain
//						.getDataDomainID()))).getHumanReadableRecordIDType(), davidID);
//		if (sGeneSymbol == "" || sGeneSymbol == null)
//			sGeneSymbol = "Unkonwn Gene";
//		addHeading(sGeneSymbol);
		
		if (PathwayManager.get().isPathwayLoadingFinished()) {
			LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
			loadPathwaysByGeneItem.setDavid(dataDomain.getPrimaryRecordMappingType(),
					davidID, dataDomain.getDataDomainID());
			addContextMenuItem(loadPathwaysByGeneItem);

			// FIXME: make setter consistent
			ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
			showPathwaysByGeneItem.setDavidID(dataDomain.getPrimaryRecordMappingType(),
					davidID, dataDomain.getDataDomainID());
			addContextMenuItem(showPathwaysByGeneItem);
		}
	}
}
