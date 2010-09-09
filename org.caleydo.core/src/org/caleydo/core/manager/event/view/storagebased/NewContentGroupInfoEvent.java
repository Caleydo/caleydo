package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewContentGroupInfoEvent
	extends AEvent {

	private ContentVAType vaType = null;
	private ContentGroupList groupList = null;
	private boolean bDeleteTree = false;
	private int setID;

	public NewContentGroupInfoEvent() {
	}

	@Override
	public boolean checkIntegrity() {

		if (vaType != null && bDeleteTree == true)
			return true;
		else if (vaType != null && groupList != null && bDeleteTree == false)
			return true;
		else
			return false;
	}

	public void setVAType(ContentVAType vaType) {
		this.vaType = vaType;
	}

	public ContentVAType getVAType() {
		return vaType;
	}

	public void setGroupList(ContentGroupList groupList) {
		this.groupList = groupList;
	}

	public ContentGroupList getGroupList() {
		return groupList;
	}

	public void setDeleteTree(boolean bDeleteTree) {
		this.bDeleteTree = bDeleteTree;
	}

	public boolean isDeleteTree() {
		return bDeleteTree;
	}

	public int getSetID() {
		return setID;
	}

	public void setSetID(int setID) {
		this.setID = setID;
	}

}
