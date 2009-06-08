package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class RemoveUnselectedGlyphsEvent
	extends AEvent {

	int iViewID;

	public RemoveUnselectedGlyphsEvent(int iViewID) {
		super();
		this.iViewID = iViewID;
	}
	
	public int getViewID() {
		return iViewID;
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
