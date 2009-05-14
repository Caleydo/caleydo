package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class RemoveUnselectedGlyphsEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
