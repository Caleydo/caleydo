package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event specifies the maximum hierarchy depth that RadialHierarchy displays.
 * 
 * @author Christian Partl
 */
public class SetMaxDisplayedHierarchyDepthEvent
	extends AEvent {

	private int iMaxDisplayedHierarchyDepth = -1;

	public int getMaxDisplayedHierarchyDepth() {
		return iMaxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {
		this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
	}

	@Override
	public boolean checkIntegrity() {
		if (iMaxDisplayedHierarchyDepth == -1)
			throw new IllegalStateException("iMaxDisplayedHierarchyDepth was not set");
		return true;
	}

}
