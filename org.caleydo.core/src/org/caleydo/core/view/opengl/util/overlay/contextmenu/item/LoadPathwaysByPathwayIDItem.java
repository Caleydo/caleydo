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

	/**
	 * Constructor. Creates the events associated with the item.
	 * 
	 * @param pathwayID
	 */
	public LoadPathwaysByPathwayIDItem(int pathwayID) {
		setPathwayID(pathwayID);
	}

	private void setPathwayID(int pathwayID) {
		setText(GeneralManager.get().getPathwayManager().getItem(pathwayID).getTitle());
		setIconTexture(EIconTextures.LOAD_DEPENDING_PATHWAYS);
		LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
		loadPathwayEvent.setSender(this);
		loadPathwayEvent.setPathwayID(pathwayID);
		registerEvent(loadPathwayEvent);
	}

}
