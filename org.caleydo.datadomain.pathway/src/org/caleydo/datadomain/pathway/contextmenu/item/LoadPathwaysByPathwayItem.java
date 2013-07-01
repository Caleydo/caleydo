/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.item;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwayEvent;

/**
 * Implementation of ContextMenuItem for loading pathways by pathway IDs. Automatically creates the events.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class LoadPathwaysByPathwayItem extends AContextMenuItem {

	private int numberOfOccurences = 0;

	/**
	 * Constructor. Creates the events associated with the item.
	 */
	public LoadPathwaysByPathwayItem(PathwayGraph pathway) {
		setPathway(pathway);
	}

	/**
	 * Constructor. Creates the events associated with the item and the number of occurrences.
	 */
	public LoadPathwaysByPathwayItem(PathwayGraph pathway, String dataDomainID, int numberOfOccurences) {

		this.numberOfOccurences = numberOfOccurences;
		setPathway(pathway);
	}

	private void setPathway(PathwayGraph pathway) {

		String pathwayName = pathway.getTitle();
		if (numberOfOccurences == 0)
			setLabel(pathwayName);
		else
			setLabel("(" + numberOfOccurences + ") " + pathwayName);


		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathway.getID());
		registerEvent(loadPathwayEvent);
	}
}
