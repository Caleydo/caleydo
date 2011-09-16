package org.caleydo.core.view.vislink;

import org.caleydo.core.event.AEvent;

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
