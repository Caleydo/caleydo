package org.caleydo.core.event.view.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * This event specifies the maximum hierarchy depth that RadialHierarchy displays.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
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
