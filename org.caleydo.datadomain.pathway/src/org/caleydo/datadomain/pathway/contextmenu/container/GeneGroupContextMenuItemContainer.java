/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.container;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItemContainer;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.item.ShowPathwaysByGenesItem;

public class GeneGroupContextMenuItemContainer extends AContextMenuItemContainer {

	public void setData(IDType idType, List<Integer> genes) {

		// AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark Genes",
		// idType, genes,
		// dataDomain.getDataDomainID());
		// addContextMenuItem(menuItem);

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE);

		if (pathwayDataDomain != null) {

			ShowPathwaysByGenesItem pathwaysItem = new ShowPathwaysByGenesItem();
			pathwaysItem.setTableIDs(dataDomain, idType, genes);
			addContextMenuItem(pathwaysItem);
		}
	}
}
