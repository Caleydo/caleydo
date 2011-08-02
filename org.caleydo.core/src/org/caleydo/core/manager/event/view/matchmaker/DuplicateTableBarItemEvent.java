package org.caleydo.core.manager.event.view.matchmaker;

import org.caleydo.core.manager.event.AEvent;

public class DuplicateTableBarItemEvent
	extends AEvent {

	private Integer itemID;

	public DuplicateTableBarItemEvent(int itemID) {
		this.itemID = itemID;
	}

	@Override
	public boolean checkIntegrity() {
		return itemID != null;
	}

	public int getItemID() {
		return itemID;
	}

}
