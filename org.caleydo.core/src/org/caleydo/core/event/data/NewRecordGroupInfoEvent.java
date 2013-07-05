/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 *
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewRecordGroupInfoEvent
	extends AEvent {

	private String perspectiveID = null;
	private GroupList groupList = null;
	private boolean bDeleteTree = false;

	public NewRecordGroupInfoEvent() {
	}

	@Override
	public boolean checkIntegrity() {

		if (perspectiveID != null && bDeleteTree == true)
			return true;
		else if (perspectiveID != null && groupList != null && bDeleteTree == false)
			return true;
		else
			return false;
	}

	public void setVAType(String vaType) {
		this.perspectiveID = vaType;
	}

	public String getVAType() {
		return perspectiveID;
	}

	public void setGroupList(GroupList groupList) {
		this.groupList = groupList;
	}

	public GroupList getGroupList() {
		return groupList;
	}

	public void setDeleteTree(boolean bDeleteTree) {
		this.bDeleteTree = bDeleteTree;
	}

	public boolean isDeleteTree() {
		return bDeleteTree;
	}
}
