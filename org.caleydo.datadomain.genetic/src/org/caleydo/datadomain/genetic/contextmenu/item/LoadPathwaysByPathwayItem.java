package org.caleydo.datadomain.genetic.contextmenu.item;

import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Implementation of ContextMenuItem for loading pathways by pathway IDs.
 * Automatically creates the events.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class LoadPathwaysByPathwayItem extends ContextMenuItem {

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
	public LoadPathwaysByPathwayItem(String label, PathwayGraph pathway, int numberOfOccurences) {
		this.numberOfOccurences = numberOfOccurences;
		setPathway(pathway);
	}

	private void setPathway(PathwayGraph pathway) {

		String pathwayName = pathway.getTitle();
		if (numberOfOccurences == 0)
			setLabel(pathwayName);
		else
			setLabel("(" + numberOfOccurences + ") " + pathwayName);

//		if (pathway.getType() == EPathwayDatabaseType.KEGG)
//			setIconTexture(EIconTextures.CM_KEGG);
//		else if (pathway.getType() == EPathwayDatabaseType.BIOCARTA)
//			setIconTexture(EIconTextures.CM_BIOCARTA);

		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathway.getID());
		registerEvent(loadPathwayEvent);
	}

}
