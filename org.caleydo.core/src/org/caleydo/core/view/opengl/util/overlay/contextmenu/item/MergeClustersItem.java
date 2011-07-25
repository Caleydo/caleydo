package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.MergeContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeDimensionGroupsEvent;
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
		setText("Merge Groups");
	}

	/**
	 * Depending on which group info should be handled a boolean has to be dataTable. True for genes, false for
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
