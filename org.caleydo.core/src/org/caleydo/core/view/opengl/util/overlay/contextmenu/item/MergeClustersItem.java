package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Merge clusters
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
		setIconTexture(EIconTextures.LOAD_DEPENDING_PATHWAYS);
		setText("Merge Clusters");
	}

	public void setGeneExperimentFlag(boolean bGeneGroup) {

		MergeGroupsEvent mergeGroupEvent = new MergeGroupsEvent();
		mergeGroupEvent.setSender(this);
		mergeGroupEvent.setGeneExperimentFlag(bGeneGroup);
		registerEvent(mergeGroupEvent);
	}
}
