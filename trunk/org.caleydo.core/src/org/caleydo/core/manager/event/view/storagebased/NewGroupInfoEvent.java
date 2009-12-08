package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewGroupInfoEvent
	extends AEvent {

	private EVAType eVAType = null;
	private GroupList groupList = null;
	private boolean bDeleteTree = false;

	@Override
	public boolean checkIntegrity() {

		if (eVAType != null && bDeleteTree == true)
			return true;
		else if (eVAType != null && groupList != null && bDeleteTree == false)
			return true;
		else
			return false;
	}

	public void setEVAType(EVAType eVAType) {
		this.eVAType = eVAType;
	}

	public EVAType getVAType() {
		return eVAType;
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
