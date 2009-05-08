package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Merge clusters
 * 
 * @author Bernhard Schlegl
 */
public class InterchangeGroupsItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public InterchangeGroupsItem() {
		super();
		setIconTexture(EIconTextures.LOAD_DEPENDING_PATHWAYS);
		setText("Interchange Groups");
	}

	public void setGeneExperimentFlag(boolean bGeneGroup) {

		InterchangeGroupsEvent interchangeGroupsEvent = new InterchangeGroupsEvent();
		interchangeGroupsEvent.setSender(this);
		interchangeGroupsEvent.setGeneExperimentFlag(bGeneGroup);
		registerEvent(interchangeGroupsEvent);
	}
}
