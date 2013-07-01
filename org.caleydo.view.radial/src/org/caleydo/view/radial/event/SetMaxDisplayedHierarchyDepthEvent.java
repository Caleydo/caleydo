/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.event;

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
