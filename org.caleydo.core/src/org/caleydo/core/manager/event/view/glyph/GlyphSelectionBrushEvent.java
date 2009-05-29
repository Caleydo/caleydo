package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class GlyphSelectionBrushEvent
	extends AEvent {

	private int brushSize = -1;

	public GlyphSelectionBrushEvent(int brushSize) {
		this.brushSize = brushSize;
	}
	
	public int getBrushSize() {
		return brushSize;
	}

	@Override
	public boolean checkIntegrity() {
		if (brushSize == -1)
			return false;
		return true;
	}

}
