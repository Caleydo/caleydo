package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewRecordGroupInfoEvent
	extends AEvent {

	private String vaType = null;
	private RecordGroupList groupList = null;
	private boolean bDeleteTree = false;
	private int dataTableID;

	public NewRecordGroupInfoEvent() {
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

	public void setVAType(String vaType) {
		this.vaType = vaType;
	}

	public String getVAType() {
		return vaType;
	}

	public void setGroupList(RecordGroupList groupList) {
		this.groupList = groupList;
	}

	public RecordGroupList getGroupList() {
		return groupList;
	}

	public void setDeleteTree(boolean bDeleteTree) {
		this.bDeleteTree = bDeleteTree;
	}

	public boolean isDeleteTree() {
		return bDeleteTree;
	}

	public int getDataTableID() {
		return dataTableID;
	}

	public void setDataTableID(int dataTableID) {
		this.dataTableID = dataTableID;
	}

}
