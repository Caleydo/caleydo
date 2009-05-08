package org.caleydo.core.manager.event.view.group;

import org.caleydo.core.manager.event.AEvent;

public class MergeGroupsEvent
	extends AEvent {

	private boolean bGeneGroup;

	public void setGeneExperimentFlag(boolean bGeneGroup) {
		this.bGeneGroup = bGeneGroup;
	}

	public boolean isGeneGroup() {
		return bGeneGroup;
	}

}
