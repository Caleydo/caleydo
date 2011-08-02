package org.caleydo.core.view.contextmenu.item;

import org.caleydo.core.manager.event.view.group.MergeContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeDimensionGroupsEvent;
import org.caleydo.core.view.contextmenu.ContextMenuItem;

/**
 * Item for merging to groups/clusters
 * 
 * @author Bernhard Schlegl
 */
public class MergeClustersItem
	extends ContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public MergeClustersItem() {
		// setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setLabel("Merge Groups");
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
			MergeContentGroupsEvent mergeGroupEvent = new MergeContentGroupsEvent();
			mergeGroupEvent.setSender(this);
			registerEvent(mergeGroupEvent);
		}
		else {
			MergeDimensionGroupsEvent mergeGroupEvent = new MergeDimensionGroupsEvent();
			mergeGroupEvent.setSender(this);
			registerEvent(mergeGroupEvent);
		}
	}
}
