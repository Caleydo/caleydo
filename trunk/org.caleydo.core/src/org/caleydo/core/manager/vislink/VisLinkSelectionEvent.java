package org.caleydo.core.manager.vislink;

import org.caleydo.core.manager.event.AEvent;

public class VisLinkSelectionEvent
	extends AEvent {

	String selectionId;
	
	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getSelectionId() {
		return selectionId;
	}

	public void setSelectionId(String selectionId) {
		this.selectionId = selectionId;
	}

}
