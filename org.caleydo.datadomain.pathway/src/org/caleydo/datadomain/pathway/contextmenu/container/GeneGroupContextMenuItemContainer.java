/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
