package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.ExportGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item for exporting groups/clusters
 * 
 * @author Bernhard Schlegl
 */
public class ExportClustersItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ExportClustersItem() {
		super();
		setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setText("Export Groups");
	}

	/**
	 * Depending on which group info should be handled a boolean has to be set. True for genes, false for
	 * experiments
	 * 
	 * @param bGeneGroup
	 *            if true gene groups will be handled, if false experiment groups
	 */
	public void setGeneExperimentFlag(boolean bGeneGroup) {

		ExportGroupsEvent exportGroupEvent = new ExportGroupsEvent();
		exportGroupEvent.setSender(this);
		exportGroupEvent.setGeneExperimentFlag(bGeneGroup);
		registerEvent(exportGroupEvent);
	}
}
