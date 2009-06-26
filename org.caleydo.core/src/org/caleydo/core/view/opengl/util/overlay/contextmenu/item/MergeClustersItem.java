package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item for merging to groups/clusters
 * 
 * @author Bernhard Schlegl
 */
public class MergeClustersItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public MergeClustersItem() {
		super();
		setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setText("Merge Clusters");
	}

	/**
	 * Depending on which group info should be handeld a bollean has to be set. True for genes, false for
	 * experiments
	 * 
	 * @param bGeneGroup
	 *            if true gene groups will be handled, if false experiment groups
	 */
	public void setGeneExperimentFlag(boolean bGeneGroup) {

		MergeGroupsEvent mergeGroupEvent = new MergeGroupsEvent();
		mergeGroupEvent.setSender(this);
		mergeGroupEvent.setGeneExperimentFlag(bGeneGroup);
		registerEvent(mergeGroupEvent);
	}
}
