package org.caleydo.datadomain.pathway.contextmenu.container;

import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.AContextMenuItemContainer;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.datadomain.pathway.contextmenu.item.ShowPathwaysByGeneItem;

/**
 * Implementation of AItemContainer for Genes. By passing an ID all relevant
 * context menu items are constructed automatically
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GeneRecordContextMenuItemContainer extends AContextMenuItemContainer {

	public void setData(IDType idType, int id) {

		AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark Gene: "
				+ dataDomain.getRecordLabel(idType, id), idType, id,
				dataDomain.getDataDomainID());
		addContextMenuItem(menuItem);

		Set<Integer> davids = ((GeneticDataDomain) dataDomain).getGeneIDMappingManager()
				.getIDAsSet(idType, dataDomain.getPrimaryRecordMappingType(), id);
		if (davids == null)
			return;
		for (Integer david : davids) {
			createMenuContent(david);
		}
	}

	private void createMenuContent(int davidID) {

		// String sGeneSymbol = generalManager.getIDMappingManager().getID(
		// dataDomain.getPrimaryRecordMappingType(),
		// ((GeneticDataDomain)
		// (DataDomainManager.get().getDataDomainByID(dataDomain
		// .getDataDomainID()))).getHumanReadableRecordIDType(), davidID);
		// if (sGeneSymbol == "" || sGeneSymbol == null)
		// sGeneSymbol = "Unkonwn Gene";
		// addHeading(sGeneSymbol);

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE);

		if (pathwayDataDomain != null) {
			LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
			loadPathwaysByGeneItem.setDavid(dataDomain.getPrimaryRecordMappingType(),
					davidID, dataDomain.getDataDomainID());
			addContextMenuItem(loadPathwaysByGeneItem);

			// FIXME: make setter consistent
			ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem(
					pathwayDataDomain);
			showPathwaysByGeneItem.setDavidID(dataDomain.getPrimaryRecordMappingType(),
					davidID, dataDomain.getDataDomainID());
			addContextMenuItem(showPathwaysByGeneItem);
		}
	}
}
