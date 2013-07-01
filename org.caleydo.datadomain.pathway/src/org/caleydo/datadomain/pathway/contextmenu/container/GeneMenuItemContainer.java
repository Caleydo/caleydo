/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.container;

import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.AContextMenuItemContainer;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.datadomain.pathway.contextmenu.item.ShowPathwaysByGeneItem;

/**
 * Implementation of AItemContainer for Genes. By passing an ID all relevant context menu items are constructed
 * automatically.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GeneMenuItemContainer extends AContextMenuItemContainer {

	public void setData(IDType idType, int id) {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);

		String label = mappingManager.getID(idType, idType.getIDCategory().getHumanReadableIDType(), id);

		label = "Bookmark gene: " + label;

		AContextMenuItem menuItem = new BookmarkMenuItem(label, idType, id);
		addContextMenuItem(menuItem);

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		Set<Integer> davids = ((GeneticDataDomain) dataDomain).getGeneIDMappingManager().getIDAsSet(idType,
				pathwayDataDomain.getDavidIDType(), id);
		if (davids == null)
			return;
		for (Integer david : davids) {
			createMenuContent(david);
		}
	}

	private void createMenuContent(int davidID) {

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		if (pathwayDataDomain != null) {
			LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
			loadPathwaysByGeneItem
					.setDavidID(pathwayDataDomain.getDavidIDType(), davidID, dataDomain.getDataDomainID());
			addContextMenuItem(loadPathwaysByGeneItem);

			ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem(pathwayDataDomain);
			showPathwaysByGeneItem.setDavidID(pathwayDataDomain.getDavidIDType(), davidID);
			addContextMenuItem(showPathwaysByGeneItem);
		}
	}
}
