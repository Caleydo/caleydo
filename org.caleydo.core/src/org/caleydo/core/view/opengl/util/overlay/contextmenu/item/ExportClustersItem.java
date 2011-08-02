package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.ExportContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.ExportDimensionGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;

/**
 * Item for exporting groups/clusters
 * 
 * @author Bernhard Schlegl
 */
public class ExportClustersItem
	extends ContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ExportClustersItem() {
		// setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setLabel("Export Groups");
	}

	/**
	 * Depending on which group info should be handled a boolean has to be table. True for genes, false for
	 * experiments
	 * 
	 * @param bGeneGroup
	 *            if true gene groups will be handled, if false experiment groups
	 */
	public void setGeneExperimentFlag(boolean bGeneGroup) {

		if (bGeneGroup) {
			ExportContentGroupsEvent exportGroupEvent = new ExportContentGroupsEvent();
			exportGroupEvent.setSender(this);
			registerEvent(exportGroupEvent);
		}
		else {
			ExportDimensionGroupsEvent exportGroupEvent = new ExportDimensionGroupsEvent();
			exportGroupEvent.setSender(this);
			registerEvent(exportGroupEvent);
		}
	}
}
