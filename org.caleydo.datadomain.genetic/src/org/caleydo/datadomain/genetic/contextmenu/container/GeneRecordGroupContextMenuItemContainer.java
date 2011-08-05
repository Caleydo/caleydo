package org.caleydo.datadomain.genetic.contextmenu.container;

import java.util.ArrayList;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.AContextMenuItemContainer;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.datadomain.genetic.contextmenu.item.ShowPathwaysByGenesItem;

public class GeneRecordGroupContextMenuItemContainer extends AContextMenuItemContainer {

	public void setData(IDType idType, ArrayList<Integer> genes) {
		
		AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark Genes",
				idType, genes, dataDomain.getDataDomainID());
		addContextMenuItem(menuItem);
		
		ShowPathwaysByGenesItem pathwaysItem = new ShowPathwaysByGenesItem();
		pathwaysItem.setTableIDs(dataDomain, idType, genes);
		addContextMenuItem(pathwaysItem);
	}
}
