package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.StorageGroupList;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewStorageGroupInfoEvent
	extends AEvent {

	private StorageVAType vaType = null;
	private StorageGroupList groupList = null;
	private boolean bDeleteTree = false;

	@Override
	public boolean checkIntegrity() {

		if (vaType != null && bDeleteTree == true)
			return true;
		else if (vaType != null && groupList != null && bDeleteTree == false)
			return true;
		else
			return false;
	}

	public void setVAType(StorageVAType vaType) {
		this.vaType = vaType;
	}

	public StorageVAType getVAType() {
		return vaType;
	}

	public void setGroupList(StorageGroupList groupList) {
		this.groupList = groupList;
	}

	public StorageGroupList getGroupList() {
		return groupList;
	}

	public void setDeleteTree(boolean bDeleteTree) {
		this.bDeleteTree = bDeleteTree;
	}

	public boolean isDeleteTree() {
		return bDeleteTree;
	}

}
