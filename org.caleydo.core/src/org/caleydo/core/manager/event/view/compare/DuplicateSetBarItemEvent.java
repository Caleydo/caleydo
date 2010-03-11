package org.caleydo.core.manager.event.view.compare;

import org.caleydo.core.manager.event.AEvent;

public class DuplicateSetBarItemEvent
	extends AEvent {
	
	private Integer itemID; 
	
	public DuplicateSetBarItemEvent(int itemID) {
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
