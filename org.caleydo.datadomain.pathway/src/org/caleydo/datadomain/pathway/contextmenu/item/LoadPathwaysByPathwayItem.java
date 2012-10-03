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
package org.caleydo.datadomain.pathway.contextmenu.item;

import org.caleydo.core.event.view.pathway.LoadPathwayEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Implementation of ContextMenuItem for loading pathways by pathway IDs.
 * Automatically creates the events.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class LoadPathwaysByPathwayItem extends AContextMenuItem {

	private int numberOfOccurences = 0;

	private String dataDomainID;

	/**
	 * Constructor. Creates the events associated with the item.
	 */
	public LoadPathwaysByPathwayItem(PathwayGraph pathway, String dataDomainID) {
		this.dataDomainID = dataDomainID;
		setPathway(pathway);
	}

	/**
	 * Constructor. Creates the events associated with the item and the number
	 * of occurrences.
	 */
	public LoadPathwaysByPathwayItem(PathwayGraph pathway, String dataDomainID,
			int numberOfOccurences) {

		this.numberOfOccurences = numberOfOccurences;
		this.dataDomainID = dataDomainID;
		setPathway(pathway);
	}

	private void setPathway(PathwayGraph pathway) {

		String pathwayName = pathway.getTitle();
		if (numberOfOccurences == 0)
			setLabel(pathwayName);
		else
			setLabel("(" + numberOfOccurences + ") " + pathwayName);

		// if (pathway.getType() == EPathwayDatabaseType.KEGG)
		// setIconTexture(EIconTextures.CM_KEGG);
		// else if (pathway.getType() == EPathwayDatabaseType.BIOCARTA)
		// setIconTexture(EIconTextures.CM_BIOCARTA);

		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathway.getID());
		loadPathwayEvent.setDataDomainID(dataDomainID);
		registerEvent(loadPathwayEvent);
	}
}
