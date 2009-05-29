package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class GlyphChangePersonalNameEvent
	extends AEvent {

	private String personalName = null;

	public GlyphChangePersonalNameEvent(String personalName) {
		this.personalName = personalName;
	}

	public String getPersonalName() {
		return personalName;
	}

	@Override
	public boolean checkIntegrity() {
		if (personalName == null)
			return false;
		return true;
	}

}
