package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.general.GeneralManager;
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
		String pathwayName = GeneralManager.get().getPathwayManager().getItem(pathwayID).getTitle();
		if (numberOfOccurences == 0)
			setText(pathwayName);
		else
			setText("(" + numberOfOccurences + ")" + pathwayName);

		setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathwayID);
		registerEvent(loadPathwayEvent);
	}

}
