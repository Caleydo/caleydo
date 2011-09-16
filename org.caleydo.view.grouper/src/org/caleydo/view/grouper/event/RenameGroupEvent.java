package org.caleydo.view.grouper.event;

import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 * 
 */
public class RenameGroupEvent extends AEvent {

	int groupID = -1;

	public RenameGroupEvent() {
	};

	public RenameGroupEvent(int groupID) {
		this.groupID = groupID;
	};

	/**
	 * @param groupID
	 *            the groupID to set
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	/**
	 * @return the groupID
	 */
	public int getGroupID() {
		return groupID;
	}

	@Override
	public boolean checkIntegrity() {
		if (groupID > 0)
			return true;
		return false;
	}

}
