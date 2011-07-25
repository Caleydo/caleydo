package org.caleydo.datadomain.genetic.contextmenu.container;

import java.util.ArrayList;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.datadomain.genetic.contextmenu.item.ShowPathwaysByGenesItem;

public class GeneRecordGroupMenuItemContainer extends AItemContainer {

	public GeneRecordGroupMenuItemContainer() {

	}

	public void setGeneIDs(IDType idType, ArrayList<Integer> genes) {
		ShowPathwaysByGenesItem pathwaysItem = new ShowPathwaysByGenesItem();
		pathwaysItem.dataTableIDs(dataDomain, idType, genes);
		addContextMenuItem(pathwaysItem);
	}

}
