package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class GlyphChangePersonalNameEvent
	extends AEvent {

	private String personalName = null;
	int iViewID;

	public GlyphChangePersonalNameEvent(int iViewID, String personalName) {
		super();
		this.iViewID = iViewID;
		this.personalName = personalName;
	}

	public String getPersonalName() {
		return personalName;
	}
	
	public int getViewID() {
		return iViewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (personalName == null)
			return false;
		return true;
	}

}
