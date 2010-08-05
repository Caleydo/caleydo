package org.caleydo.datadomain.genetic.contextmenu.item;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Implementation of AContextMenuItem for loading pathways by pathway IDs. Automatically creates the events.
 * 
 * @author Alexander Lex
 */
public class LoadPathwaysByPathwayIDItem
	extends AContextMenuItem {

	private int numberOfOccurences = 0;

	/**
	 * Constructor. Creates the events associated with the item.
	 * 
	 * @param pathwayID
	 */
	public LoadPathwaysByPathwayIDItem(int pathwayID) {
		setPathwayID(pathwayID);
	}

	public LoadPathwaysByPathwayIDItem(int pathwayID, int numberOfOccurences) {
		this.numberOfOccurences = numberOfOccurences;
		setPathwayID(pathwayID);
	}

	private void setPathwayID(int pathwayID) {
		PathwayGraph pathway = GeneralManager.get().getPathwayManager().getItem(pathwayID);

		String pathwayName = pathway.getTitle();
		if (numberOfOccurences == 0)
			setText(pathwayName);
		else
			setText("(" + numberOfOccurences + ") " + pathwayName);

		if (pathway.getType() == EPathwayDatabaseType.KEGG)
			setIconTexture(EIconTextures.CM_KEGG);
		else if (pathway.getType() == EPathwayDatabaseType.BIOCARTA)
			setIconTexture(EIconTextures.CM_BIOCARTA);

		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathwayID);
		registerEvent(loadPathwayEvent);
	}

}
